# Releases

* **V1.0.0**	:	Forth and Lastest version of application with upgrade tests and documentation:

1. Update relationship between Person and fireStation to M:M
2. Update Model Response to PersonDto for more readable response for each URL's endpoint
3. Update and check all UT and IT for the application
4. Creation of Api Documentation with swagger2 and mvn site with reports
5. Management of Actuator:health,info,trace and metrics


* **V0.2.0**	:	Third version with creation of RestController for Emergency's services using TDD:

1. creation of Rest Controller for emergencies' services that manage URLS (fireStation,childAlert,phoneAlert,fire,flood,personInfo and communityEmail)
2. creation of Model Response to make more easily readable the body of each URLS's ResponseEntity
3. activation and configuration of actuator for health, info, metrics and httpTrace
4. implementation of javadoc and check for chekstyle rules.


* **V0.1.0**	: 	Second version, implementation of code using TDD:

1. creation of Service to load Data from a source using a strategy pattern in CommandLineRunner.
2. creation of Rest Controller for administration that manages Entities Person,FireStation,MedicalRecord, Medication and Allergy.
3. creation of global handler Exceptions to manage exceptions from Controllers 
4. implementation of javadoc and update code to check checkStyle's rules.

	
* **V0.0.1**	: 	First version of the project. implementation of Structure only.	
