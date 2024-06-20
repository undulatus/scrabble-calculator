Scrabble Calculator
=============

This is an exam entry application made by - Bryan Matthew Batanes

Table of Contents
-----------------

1. Introduction
2. Features
3. Requirements
4. Installation
5. Running the Application
6. Testing
7. Configuration
8. Usage

Introduction
------------

A simple MS for handling scrabble score calculation, saving, viewing top 10 scores.

Features
--------

- RESTful API
- CRUD operations
- Database integration with Spring Data JPA
- Unit and integration testing with JUnit and Mockito

Requirements
------------

- Java 17
- Maven 3.6.0 or higher

Installation
------------

1. Clone the repository:

   git clone https://github.com/undulatus/scrabble-calculator.git
   cd scrabble-calculator

2. Build the project using Maven:

   mvn clean install

Running the Application
-----------------------

You can run the application in several ways:

1. Using Maven:

   mvn spring-boot:run

2. Using Java:

   java -jar target/scrabble-0.0.1-SNAPSHOT.jar

Testing
-------

To run unit and integration tests, use the following command:

    mvn test

Configuration
-------------

The application uses a configuration file located at `src/main/resources/application.properties`.

Please configure database connection for your local example below

    # Database configuration
    spring.datasource.url=jdbc:postgresql://localhost:5433/scrabbledb
    spring.datasource.username=postgres
    spring.datasource.password=kek3
    spring.datasource.driver-class-name=org.postgresql.Driver

Usage
-----

After starting the application, you can access the API documentation (if using Swagger) at:

    http://localhost:8080/swagger-ui.html

Endpoints:

- `GET /scrabble/scores` - Fetch all scores saved
- `POST /scrabble/scores` - Save the current word's score
- `POST /scrabble/letterpoints/setup` - Create letters and points scoring system
- `GET/scrabble/scores/top10` - Fetch all top 10 scores in descending points order
- `GET /scrabble/scores/calculate` - Calculate score of the word
- `GET /scrabble/letterpoints` - Fetch letters and points scoring system
- `DELETE /scrabble/letterpoints/remove` - Remove existing letters and points scoring system


