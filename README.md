# Flight Booking System
Flight Booking System is a Java application for managing flight bookings.
It provides functionality for users to register, book flights, and manage their bookings. 
The application is built using Java 17 and utilizes a PostgreSQL database. 
It can be run locally with configuration settings specified in the application.properties file or deployed using Docker with the command docker-compose up.

## User and Admin Controller
The User and Admin Controller provides endpoints for user registration, login, updating user information, and registering administrators. Users can register with the roles CLIENT or ADMIN, and upon registration, a unique UUID is assigned to each user. User passwords are encrypted using bcrypt technology. Login requires a JWT token, which is generated during the login process. Users can update their email and password information.

## Flight Controller
The Flight Controller allows administrators to manage flights in the application. Administrators can create new flights, update existing flights, delete flights, and retrieve flight information. These operations require a JWT token for authentication, which should be set in the Authorization header as a Bearer token. The controller also provides the ability to search for flights based on departure and arrival airports, as well as date and time.

## Booking Controller
The Booking Controller enables users to book seats for flights, retrieve their bookings, and cancel bookings. Users can book seats by specifying the flight ID and the number of seats. The operations require a JWT token for authentication. Users can retrieve a list of all their bookings and cancel a booking by providing the booking ID. Cancelling a booking increases the seat count for the corresponding flight in the database.

## Technology Stack
* Java 17
* PostgreSQL
* Docker (optional)

## Getting Started
Set up a PostgreSQL database and configure the connection settings in the application.properties file.
Run the application locally using the configured settings or use Docker with the command docker-compose up.
Please refer to the API documentation or code comments for more detailed information on each endpoint and request/response formats.

-> The application will be available at http://localhost:8080/swagger-ui/index.html
