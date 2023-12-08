# Base image
FROM gradle:6.9.0-jdk11
# Set working directory so that all subsequent command runs in this folder
WORKDIR /test-ehr
# Copy app files to container
COPY --chown=gradle:gradle . .
RUN gradle build
# Expose port to access the app
EXPOSE 8080

#HealthCheck 
RUN apt update 
RUN apt upgrade
RUN apt search curl 
RUN apt add curl
HEALTHCHECK --interval=60s --timeout=10m --retries=5 CMD curl --fail http://localhost:8080 || exit 1

# Command to run our app
CMD ./dockerRunnerProd.sh