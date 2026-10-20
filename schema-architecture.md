This Spring Boot application uses both MVC and REST controllers.
Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules.
The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). 
All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories. 
MySQL uses JPA entities while MongoDB uses document models.

1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate Thymeleaf or REST controller.
3. The controller calls the service layer...
4.The Service Layer then communicates with the Repository Layer to perform data access operations through MySQL Repostiories or MongoDB Repository.
5.The repositories access the MySQL Database for all the core entitites or the MongoDB Database prescriptions
6.Data is then converted to JPA entities in the case of MySQL or loaded into document objects for MongoDB
7.Then the Models are used in the response layerto render HTML pages through Thymeleaf or return JSON responses through REST APIs.
