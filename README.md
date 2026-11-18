# API Playground Builder

## Project Description
API Playground Builder is a Java-based web application developed to execute and manage API requests through a user-friendly interface. Users can register, log in, execute API requests, view responses, save requests, and access their request history.

## Technologies Used
- **Programming Language:** Java
- **Frontend:** HTML, CSS, JavaScript, JSP
- **Backend:** Java Servlets
- **Database Connectivity:** JDBC
- **Database:** MySQL / MariaDB
- **Server:** Apache Tomcat 11
- **IDE:** NetBeans IDE

## Features
- User Registration and Login
- Session Management
- User Dashboard
- API Request Execution
- View API Responses
- Save API Requests
- Update and Delete Saved Requests
- Request History

## Project Structure
- `src/` – Contains Java source files, Servlets, DAO classes, database connection, and utility classes.
- `web/` – Contains JSP pages, HTML, CSS, JavaScript, and other web resources.
- `nbproject/` – Contains NetBeans project configuration files.
- `build.xml` – Contains Ant build configuration.
- `database.sql` – Database export file, if included.

## Database Details
**Database Name:** `api_playground`

The database contains tables for:
- Users
- API Requests
- Request Headers
- Request Parameters
- Query History
- Saved Requests

## Requirements
- JDK
- NetBeans IDE
- Apache Tomcat 11
- MySQL / MariaDB
- XAMPP (for local database management)

## How to Run the Project
1. Clone or download this repository.
2. Open the project in NetBeans IDE.
3. Create a database named `api_playground`.
4. Import the provided `database.sql` file into MySQL/MariaDB.
5. Configure the database credentials in `DBConnection.java`.
6. Configure Apache Tomcat 11 in NetBeans.
7. Run the project.

## Author
Developed as an Advanced Java Technologies academic project.
