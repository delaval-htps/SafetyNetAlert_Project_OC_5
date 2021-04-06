#SafetyNetAlert

SafetyNetAlert is a back-end java application that gives essential informations to emergency's services through a RESTfull API.

For examples,SafetyNetAlert can provide:
* the location of persons in a sector during a fire
* their number of phone and addresses to alert them of a weather accident
* their medical history to help emergency services to work correctly

## Running the application locally

There are several ways to run application on your local machine. One way is to execute the `main` method in the `com.safetynet.alert.SafetyNetApplication` from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```
## Access and Endpoints
Since application started, you can access to all endpoints of REST API with the URL:

```html
https://localhost:8080/
```
Below you can find list of different endpoints :their description and use are explained in REST API documentation located at:`target/documentation`

##Run test
We use `Cucumber` to create acceptance tests more easily readable for customers and above all to generate code's implementation using TDD...

So,from these acceptance tests, we created integration and unit tests to implement and check the code.

##Site and Reports

We created a site with Maven to aggregate different reports.To deploy the site use the command:

```shell
mvn site
```
and in `target/site` you will find a page index.html that you can open in your web browser witch contains these different reports:

##Application was built with

* `SpringBoot 2.4.4`
* `Java 8`
* `Maven 3.6.3`
* `H2 and Mysql databases`
* `JUnit`
* `Jacoco`
* `Log4j`

