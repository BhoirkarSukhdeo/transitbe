
# TransitBE

This Repository contains codebase for Transit app service.

## Application setup
1. IDE - download one of the IntelliJ/Eclipse ide.
2. Clone repo from here:https://github.com/hashedin/TransitBE/
3. set env variables:
    ```
    SPRING_PROFILE=<'env_type'> (dev/qa/uat/prod)
    REDIS_HOST= <'redis_host_url'> (localhost)
    REDIS_PORT=<'redis_port'> (6379)
    REDIS_DB_INDEX= <'redis_db_index'> (1)
    DB_HOST=<db_host_url> (localhost)
    DB_PORT=<db_port> (5432)
    DB_NAME=<db_name> (test_database)
    DB_USERNAME=<db_username> (test_user)
    DB_PASSWORD=<db_password> (test_user)
    JWT_SECRET_KEY=<jwt_secret_key> (transit)
    FIREBASE_SERVER_KEY=<firebase_server_key> (Obtained from firebase account)
   TRANSIT_CARD_HOST=<'transit_card_host for v1 APIs'>
   TRANSIT_CARD_HOST_V2=<'transit_card_host for v2 APIs'>
   JUSPAY_API_KEY=<'juspay_api_key'>
   JUSPAY_MID=<'juspay_merchant_id'>
   RESPONSE_KEY=<'juspay_respones_key'>
   AUTH_USER_ID=<'auth_user_id'>
   AUTH_PASSWORD=<'auth-password'>
   CARD_SECRET_KEY=<'card_secret_key'> ( encryption and decryption for card no)
   TRANSIT_SECRET_KEY=<'transit_secret_key'>(encryption and decryption for transit card APIs)
   SERVICE_REQUEST_ID=<'service_request_id'>(for every transit card apis)
   CHANNEL_ID=<'channel_id'>(transit channel id)
   KMRL_TICKET_USERNAME=<'kmrl_ticket_username'>
   KMRL_TICKET_PASSWORD=<'kmrl_ticket_password'>
   KMRL_TICKET_TOKEN=<'kmrl_ticket_token'>
   KMRL_TICKET_BASE_URL=<'kmrl_ticket_base_url'>
   AXIOM_USERID=<axiom_userid>
   AXIOM_PWD=<axiom_pwd>
   AXIOM_DCODE=<axiom_dcode>
   AXIOM_BASEURL=<axiom_baseurl>
   OSRM_BASE_URL=<osrm_url>
   SMS_PROVIDER=<sms_provider> (twillio/axiom)
   PROXY_HOST=<proxy host from on-prem>
   PROXY_PORT=<proxy port from on-prem>
   PROXY_NOPROXY_LIST=<list of urls excluded from proxy seperated by | > (eg localhost|192.168.1.2|google.com)
   FILE_PATH=<path_of_file>(eg /home/hasher/files/)
   SERVERS=<list_of_servers>(eg http://localhost:8080/transit)
   ```
    
4. IDE Setup:
    1. Import project in IDE.
    2. Run/Debug Configuration-a)Add main class -> set TransitApplication   .b) Use classpath of module-> set transit.main.
Run application.
5. Local setup:
    1. Update you gradle to latest (5.6+) 
    2. Run `gradle build` / `gradle clean build`
    3. Run `java -jar <'Path to jar file'>` (eg:`java -jar build/libs/abc.jarjar`)
6. Docker Setup:
    1. Install docker on machine
        - [For Ubuntu](https://docs.docker.com/install/linux/docker-ce/ubuntu/)
        - [For Mac](https://docs.docker.com/docker-for-mac/install/)
    2. Run  `sh docker-build.sh -t <image_tag_name> -p '<path to jar>'` (eg: `sh docker-build.sh -t transit:dev  -p 'build/libs/*jar'`) This will build your image with given tag name for given env
        - -t = Tag name for image to build (default: transit:dev)
        - -p = Path at which jar is generated (default: build/libs/*jar)
    3. Run `sh docker-run.sh -p <local_port_number> -e <env> -t <image_tag_name>`  (eg: `sh docker-run.sh -e .env -p 8081 -t transit:dev`) This will run image transit:dev on port 8081
        - -p = Port on which service must run (default: 8081).
        - -e = environment file path (File is required at git root, default: .env)
        - -t = Tag name of image built in above step (default: transit:dev)



## Database setup
We are using H2 Database(in memory database) for local database integration and local testing.
you can access H2 database console via http://localhost:8080/transit/h2-console after starting the application.


## Swagger config
Access Swagger UI via <HOST>//transit/swagger-ui.html after starting the application.

## Running all checks
Run `gradle check` -It is common for all verification tasks, including tests and linting, to be executed using the check task.

## Computing all output
Run `gradle build` -Gradle builds for the build task to designate assembling all outputs and running all checks.
./gradlew build - Using this command you can also build the application.

## Gradle unit test
1. Run `gradle test` or `./gradlew test` - Using these command you can run all unit tests.
