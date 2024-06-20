Scrabble Calculater
=============

This is an exam entry application made by Bryan Matthew Batanes

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


Usage
-----

After starting the application, you can access the API documentation (if using Swagger) at:

    http://localhost:8080/swagger-ui.html

Example Endpoints:

- `GET /api/items` - Retrieve all items
- `GET /api/items/{id}` - Retrieve an item by ID
- `POST /api/items` - Create a new item
- `PUT /api/items/{id}` - Update an existing item
- `DELETE /api/items/{id}` - Delete an item by ID


