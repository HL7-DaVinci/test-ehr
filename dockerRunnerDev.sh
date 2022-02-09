#!/bin/bash

# Handle closing application on signal interrupt (ctrl + c)
trap "kill $LOAD_DATA_PID $CONTINUOUS_BUILD_PID $SERVER_PID; gradle --stop; exit" INT

# Set environment variables
export DOCKER_PROFILE="true"

mkdir logs 
# Reset log file content for new application boot
echo "*** Logs for 'gradle installBootDist --continuous' ***" > ./logs/builder.log
echo "*** Logs for 'gradle bootRun' ***" > ./logs/runner.log

# Print that the application is starting in watch mode
echo "Starting application in watch mode..."

# Start load data process once server is running 
echo "Starting continuous data loader..."
( while ! grep -m1 "Tomcat started on port" < ./logs/runner.log; do
    sleep 1
done
echo "loading data into test-ehr..."
gradle loadData

# Continuous Load Data command whenever fhirResourcesToLoad directory changes
reources_modify_time=$(stat -c %Y fhirResourcesToLoad)
while sleep 1
do
    new_reources_modify_time=$(stat -c %Y fhirResourcesToLoad)
    
    if [[ "$reources_modify_time" != "$new_reources_modify_time" ]] 
    then
        echo "loading data into test-ehr..."
        gradle loadData
    fi

    reources_modify_time=$new_reources_modify_time

done ) & LOAD_DATA_PID=$!

# Start the continious build listener process
echo "starting continuous build listener..."
( gradle build --continuous | tee ./logs/builder.log ) & CONTINUOUS_BUILD_PID=$!

# Start server process once initial build finishes  
( while ! grep -m1 'BUILD SUCCESSFUL' < ./logs/builder.log; do
    sleep 1
done
echo "starting test-ehr server in debug mode..."
gradle bootRun -Pdebug 2>&1 | tee ./logs/runner.log ) & SERVER_PID=$!



# Handle application background process exiting
wait $CONTINUOUS_BUILD_PID $SERVER_PID $LOAD_DATA_PID
EXIT_CODE=$?
echo "application exited with exit code $EXIT_CODE..."

