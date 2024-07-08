# Model Mapping with Spring Boot

<strong>Model mapping in Spring Boot</strong> is essential for efficient data transformation between different object models, enhancing code maintainability by reducing repetition.<br/>
It promotes separation of concerns, keeping data transformation logic distinct from business logic.<br/>
<strong>Model mapping</strong> also provides flexibility for custom transformations and can improve application performance by reducing unnecessary data processing.

<b>Author:</b> <a href="https://github.com/spring-boot-react-nextjs" target="_blank">spring-boot-react-nextjs</a><br>
<b>Created:</b> 2024-07-08<br>
<b>Last updated:</b> 2024-07-08

[![](https://img.shields.io/badge/Spring%20Boot-8A2BE2)]() [![](https://img.shields.io/badge/release-Jun%2020,%202024-blue)]() [![](https://img.shields.io/badge/version-3.3.1-blue)]()

## 1. Why Model Mapping Matters?

Model mapping is a crucial aspect of Spring Boot applications for several reasons:  

1. <b>Data Transformation:</b> <strong>Model mapping</strong> helps in transforming data from one object model to another.<br/>
This is particularly useful when the data model of the incoming request is different from the model used in your application.<br/> For instance, you might receive data from a REST API in one format but need to convert it to a different format for your application's internal use.
<br/><br/>
2. <b>Code Maintainability:</b> By using <strong>model mapping</strong>, you can keep your code DRY (Don't Repeat Yourself).<br/>
Without model mapping, you might find yourself writing repetitive code to convert data from one model to another.<br/> With model mapping, you can define this conversion logic once and reuse it throughout your application.
<br/><br/>
3. <b>Separation of Concerns:</b> <strong>Model mapping</strong> allows you to separate your data transformation logic from your business logic. This makes your code easier to understand and maintain.<br/>
It also makes it easier to test your data transformation logic separately from your business logic.
<br/><br/>
4. <b>Flexibility:</b> <strong>Model mapping</strong> libraries often provide flexible and powerful features for customizing the data transformation process.<br/>
For example, you might want to transform certain fields in a specific way, ignore certain fields, or add custom logic to handle complex transformations. Model mapping libraries make these tasks easier.
<br/><br/>
5. <b>Performance:</b> <strong>Model mapping</strong> can also improve the performance of your application.<br/>
By transforming data into the exact format needed by your application, you can reduce unnecessary data processing. This can lead to faster response times and lower memory usage.

## 2. How a Model Mapping Implementation Works in Spring Boot?

Spring Boot simplifies the process of Model Mapping through the easy integration of <strong>Model Mapper</strong>. Here's how it works:

### 2.1 Create a Spring Boot Application

For this basic example we will start with a simple REST endpoint which we can call to see the <strong>Model Mapping</strong> at work.<br>
Lets create an application using the dependencies as previewed:

![01-start-spring-io](https://github.com/spring-boot-react-nextjs/spring-boot-modelmapper/blob/main/images/01-start-spring-io.png)

[![](https://img.shields.io/badge/Lombok-8A2BE2)]()
Because it is just that easy to use.
Want to know more about <b>Project Lombok</b>? [Click this link](https://projectlombok.org/features/)

[![](https://img.shields.io/badge/Spring%20Web-8A2BE2)]()
This Spring Framework dependency will provide us with all the necessary functionality to create and manage our REST endpoints.

[![](https://img.shields.io/badge/ModelMapper-8A2BE2)]()
Add the following dependency to your `pom.xml` file:
```xml
<dependency>
  <groupId>org.modelmapper</groupId>
  <artifactId>modelmapper</artifactId>
  <version>3.2.0</version>
</dependency>
```

### 2.2 Configure Model Mapper

Now that the Spring Boot application is created, it is time to configure the Model Mapper.<br/>
Within your project, create a package called `config` and add a Java class `AppConfig.java`:

```java
@Configuration
public class AppConfig {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
```

By defining the <strong>ModelMapper</strong> bean in your Spring configuration, you will be able to autowire it wherever you need it in your application.

### 2.3 Create the Model Classes

For this example, we will create two model classes: `User` and `UserDto`.<br/>
The `User` class represents the entity model, while the `UserDto` class represents the data transfer object (DTO) model.

`User.java`
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String username;
    private String email;
}
```
<br/>

`UserDto.java`
```java
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String username;
    private String email;
}
```
<br/>

### 2.4 Create the Model Mapping Service

Next, we will create a service class that will handle the model mapping logic.<br/>

`UserMapperService.java`
```java
@Service
public record UserMapperService(ModelMapper modelMapper) {

    public UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
```

The `UserMapperService` class contains two methods: `convertToDto` and `convertToEntity`.<br/>
These methods use the `ModelMapper` bean to map objects of type `User` to `UserDto` and vice versa.


<b>Notice:</b> The <b>Model Mapper</b> will automatically map the fields with the same name and type between the <b>User</b> and <b>UserDto</b> classes.

Here is an example where field names do not match, <strong>Model Mapper</strong> is able to map them by using the `@Mapping` annotation.

```java
@Mapper
public interface UserMapper {

    @Mapping(source = "username", target = "name")
    UserDto convertToDto(User user);

    @Mapping(source = "name", target = "username")
    User convertToEntity(UserDto userDto);
}
```

It even handles nested objects and collections, making it a powerful tool for complex data transformations.

### 2.5 Create the REST Controller and the UserService
    
Finally, we will create a REST controller that will expose an endpoint to demonstrate the <strong>model mapping.</strong><br/>
We will also create a `UserService` class to handle the business logic.

`UserController.java`
```java
@RestController
@RequestMapping(value = "/api/v1/users")
public record UserController(UserService userService) {

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }
}
```
<br/>

`UserService.java`
```java
@Service
public record UserService(UserMapperService userMapperService) {

    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = this.getUserList();

        List<UserDto> userDtoList = users.stream()
                .map(userMapperService::convertToDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    public List<User> getUserList() {
        ArrayList<User> users = new ArrayList<>();
        users.add(User.builder()
                .id(UUID.randomUUID())
                .username("john-doe")
                .email("john@test.com")
                .build()
        );
        users.add(User.builder()
                .id(UUID.randomUUID())
                .username("jane-doe")
                .email("jane@test.com")
                .build()
        );

        return users;
    }

    public ResponseEntity<UserDto> getUserByUsername(String username) {
        Optional<User> user = this.findUsernameInUserList(username);
        if (user.isEmpty()) {
            // throw new UserNotFoundException(username);
            // Not part of this example
        }
        return new ResponseEntity<>(
                userMapperService.convertToDto(user.get()),
                HttpStatus.OK
        );
    }

    private Optional<User> findUsernameInUserList(String username) {
        List<User> allUsers = this.getUserList();
        return allUsers.stream()
                .filter(user -> username.equals(user.getUsername()))
                .findAny();
    }

    public ResponseEntity<UserDto> createUser(UserDto userDto) {
        User newUser = userMapperService.convertToEntity(userDto);

        Optional<User> user = this.findUsernameInUserList(userDto.getUsername());
        if (user.isPresent()) {
            // throw new UserAlreadyExistsException(userDto.username());
            // Not part of this example
        }
        // Save the user to the database
        // Not part of this example
        return new ResponseEntity<>(
                userMapperService.convertToDto(newUser),
                HttpStatus.CREATED
        );
    }
}
```


## 3. Spring Boot Model Mapping In Action 

- To test the <b>REST endpoints</b>, a tool like <b>Postman</b> can be used to send <b>HTTP requests</b>.
- A Postman collection is added within the repository `src/main/resources/postman/collection-to-import.json`
<br/>

<b>Note:</b> The Postman responses will not contain the `id` field, as it is not part of the `UserDto` model.

[![](https://img.shields.io/badge/GET-green)]()<br/>
<small>Endpoint:</small> `http://localhost:8081/api/v1/users`<br/>
<small>Returns:</small> A list of all users

![02-postman-get-all-users](https://github.com/spring-boot-react-nextjs/spring-boot-modelmapper/blob/main/images/02-postman-get-all-users.png)
<br><br>

[![](https://img.shields.io/badge/GET-green)]()<br/>
<small>Endpoint:</small> `http://localhost:8081/api/v1/users/{username}`<br/>
<small>Returns:</small> The user with the specified username

![03-postman-get-by-username](https://github.com/spring-boot-react-nextjs/spring-boot-modelmapper/blob/main/images/03-postman-get-by-username.png)
<br><br>

[![](https://img.shields.io/badge/POST-yellow)]()<br/>
<small>Endpoint:</small> `http://localhost:8081/api/v1/users`<br/>
<small>Returns:</small> The newly created user

![04-postman-post-new-user](https://github.com/spring-boot-react-nextjs/spring-boot-modelmapper/blob/main/images/04-postman-post-new-user.png)
<br><br>

## Let's Stay Connected

If you have any questions in regard to this repository and/or documentation, please do reach out.

Don't forget to:
- <b>Star</b> the [repository](https://github.com/spring-boot-react-nextjs/spring-boot-modelmapper)
- [Follow me](https://github.com/spring-boot-react-nextjs) for more interesting repositories!