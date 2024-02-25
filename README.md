# Weather Tracking API

## Prerequisites
* Maven installed and usable via the command line I am using version 3.8.4
* Docker and docker-compose installed and usable via the command line (I am using Docker version 20.10.0 and docker-compose version 1.29.2)
* Java installed (I am using Java 15.0.2 2021-01-19)

Afrer the prerequisites have been met, We can run the Weather Tracking API by running the following commands in terminal, in the directory of the Weather Tracking API
```bash
mvn clean install && docker-compose up -d
```

This will run a Maven clean install, running the created unit tests and then building a jar. 
After this a docker container for the MySQL database will be built as well as a Docker container for the Weather Tracking API. 
docker-compose will first bring up the MySQL Docker container and then the Weather Tracking API. 
After both are up the Weather Tracking API is ready to start serving traffic. 
For your convenience please find a Postman collection in the 'postman' folder in the root directory of the Weather Tracking API.

To bring down the Weather Tracking API and it's accompanying MySQL database. Simply run the following command in the directory of the Weather Tracking API
```bash
docker-compose down
```
