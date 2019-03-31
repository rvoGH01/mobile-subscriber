# HOW TO RUN
1. Get all the sources from "https://github.com/rvoGH01/mobile-subscriber"
2. Tune "src/main/resources/application.yml" in order to get valid DB connection
3. Execute "mvn clean install" to build the project
4. Create database using SQL script "src/main/resources/mobile_subscriber.sql"
5. Run application with command "java -jar mobile-subscriber-1.0.0-SNAPSHOT.jar"

# SWAGGER
http://localhost:8181/swagger-ui.html

## Basic Operations

    Resource Endpoint                                                   | HTTP method  |    Description
---------------------------------------------------------------------------------------------------------------------------------------------------------------
    http://localhost:8181/api/mobile-subscribers                        |    GET       |    Get all mobile subscribers
    http://localhost:8181/api/mobile-subscribers?query=msisdn~*66       |    GET       |    Get all mobile subscribers for which msisdn ends with "66"
    http://localhost:8181/api/mobile-subscribers?query=msisdn~38067*    |    GET       |    Get all mobile subscribers for which msisdn starts with "38067"
    http://localhost:8181/api/mobile-subscribers                        |    POST      |    Add new mobile subscriber
    http://localhost:8181/api/mobile-subscribers/{id}                   |    PUT       |    Update mobile subscriber by ID
    http://localhost:8181/api/mobile-subscribers/{id}                   |    DELETE    |    Delete mobile subscriber by ID
    http://localhost:8181/api/mobile-subscribers?msisdn={msisdn}        |    DELETE    |    Delete mobile subscriber by msisdn

