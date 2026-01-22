Task Reminder App

A simple web-based Task Reminder Application built using Spring Boot that helps users manage tasks efficiently and receive reminders.
The application is currently under active development.

Features

 User authentication (login & registration)
 Create, update, and delete tasks
 Task reminders with due dates
 Contact page for feedback and issue reporting
 Automatic email acknowledgement for contact submissions
 Secure form handling with validation and spam prevention

Tech Stack

 Backend: Java, Spring Boot, Spring MVC, Spring Data JPA
 Frontend: Thymeleaf, HTML, CSS
 Database: MySQL / H2 (configurable)
 Email: Spring Boot Mail (SMTP)
 Build Tool: Maven

Getting Started

 Prerequisites
 Java 17+
 Maven
 MySQL
 An SMTP email account (for contact form emails)

Run the Application
 git clone https://github.com/DakaraiMahisa/task-reminder-app.git
 cd task-reminder-app
 mvn spring-boot:run

The application will start at:
  http://localhost:8080

Configuration:

Update application.properties with your database and email details:

 spring.datasource.url=jdbc:mysql://localhost:3306/task_reminder
 spring.datasource.username=your_db_user
 spring.datasource.password=your_db_password

 spring.mail.username=your-email@gmail.com
 spring.mail.password=your-app-password

Contact & Feedback

The application includes a Contact page where users can:
 Report bugs
 Provide feedback
 Suggest features

Messages are:
 Stored in the database
 Sent to the admin via email
 Automatically acknowledged with a reply email

Project Status:
🚧 In Development

Acknowledgement:

I would like to express my sincere appreciation to Infosys Springboard for providing the internship opportunity and learning platform that supported the development of this project.
The guidance, resources, and structured learning environment offered through the program played an important role in strengthening my practical understanding of software development using Spring Boot.

This project was developed as part of an internship learning experience and is intended for educational and skill-building purposes.

Author:
 Dakarai Mahisa
 Virtual Internship Project – Infosys Springboard.



