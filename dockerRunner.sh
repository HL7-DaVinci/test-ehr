#!/bin/bash

# Handle closing application on signal interrupt (ctrl + c)
trap "kill $LOAD_DATA_PID $CONTINUOUS_BUILD_PID $SERVER_PID; gradle --stop; exit" INT


# Reset log file content for new application boot
echo "*** Logs for 'gradle installBootDist --continuous' ***" > builder.log
echo "*** Logs for 'gradle bootRun' ***" > runner.log

# Print that the application is starting in watch mode
echo "Starting application in watch mode..."

# Start load data process once server is running 
( while ! grep -m1 "Tomcat started on port" < runner.log; do
    sleep 15
done
echo "loading data into test-ehr..."
gradle loadData ) & LOAD_DATA_PID=$!
echo "Load Data Task started in background on PID $LOAD_DATA_PID ..." # >> pid_out.txt

# Start the continious build listener process
echo "starting continuous build listener..."
gradle build --continuous | tee builder.log & CONTINUOUS_BUILD_PID=$!
echo "Continuous Build Task started in background on PID ${CONTINUOUS_BUILD_PID} ..." # >> pid_out.txt

# Start server process once initial build finishes  
( while ! grep -m1 'BUILD SUCCESSFUL' < builder.log; do
    sleep 15
done
echo "starting test-ehr server..."
gradle bootRun 2>&1 | tee runner.log ) & SERVER_PID=$!
echo "Server Start Task started in background on PID $SERVER_PID ..." # >> pid_out.txt


# Handle application background process exiting
wait $CONTINUOUS_BUILD_PID $SERVER_PID $LOAD_DATA_PID
EXIT_CODE=$?
echo "application exited with exit code $EXIT_CODE..."

