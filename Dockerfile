FROM eclipse-temurin:11-jdk-ubi9-minimal

# Keep the compatible Gradle release on an updated Java 11 base.
COPY --from=gradle:6.9.4-jdk11-ubi9 /opt/gradle /opt/gradle
ENV GRADLE_HOME=/opt/gradle
ENV PATH="${GRADLE_HOME}/bin:${PATH}"

WORKDIR /test-ehr
COPY . .
RUN gradle build
# Expose port to access the app
EXPOSE 8080

#HealthCheck 
HEALTHCHECK --interval=45s --start-period=60s --timeout=10m --retries=10 CMD curl --fail http://localhost:8080/test-ehr/r4/metadata || exit 1

# Command to run our app
CMD ./dockerRunnerProd.sh
