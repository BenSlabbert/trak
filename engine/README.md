## Running Set Up

### VM Options

* -Dspring.profiles.active=dev

### Environment Variables

#### H2

* SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL

#### MySQL

* SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/trak
* SPRING_DATASOURCE_USERNAME=root
* SPRING_DATASOURCE_PASSWORD=root
