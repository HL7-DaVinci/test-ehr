#!/bin/bash

# Handle closing application on signal interrupt (ctrl + c)
trap "kill $LOAD_DATA_PID $SERVER_PID; gradle --stop; exit" INT

# Set environment variables
export DOCKER_PROFILE="true"
mkdir logs 
# Reset log file content for new application boot
echo "*** Logs for 'gradle bootRun' ***" > ./logs/runner.log

# Print that the application is starting in watch mode
echo "Starting application in production mode..."

# Start load data process once server is running 
( while ! grep -m1 "Tomcat started on port" < ./logs/runner.log; do
    sleep 1
done
echo "loading data into test-ehr..."
gradle loadData ) & LOAD_DATA_PID=$!

# Start server process 
echo "starting test-ehr server..."
( gradle bootRun 2>&1 | tee ./logs/runner.log ) & SERVER_PID=$!

# Handle application background process exiting
wait $SERVER_PID $LOAD_DATA_PID
EXIT_CODE=$?
echo "application exited with exit code $EXIT_CODE..."

