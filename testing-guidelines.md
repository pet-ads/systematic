## Testing guidelines

General guidelines: 

- All tests must be clean 
- All tests must really test something (!!)
- Keep the test classes organized (use @Nested to organize tests by methods of valid/invalid equivalence classes)
- Avoid repeated code along the tests, create supporting test methods.
- Create test data factory methods to avoid repeatedly creating big objects inside tests

### Tagging classes and tests:

Tag all test classes and nested classes using @Tag as well as the tests that make sense to. The intention is to prepare
the project to create test suites in the future. Use the following convention:

- **@Tag("UnitTest")** for all classes that contains unit tests (e.g. SystematicStudyTest, StudyReviewTest, DoiTest)
- **@Tag("ServiceTest")** for all application services tests (e.g. CreateSystematicStudyServiceImplTest, 
CreateStudyReviewServiceImplTest)
- **@Tag("IntegrationTest")** for all controllers test and repositories implementation tests as well as other kinds of
integration tests (e.g. StudyReviewControllerTest, MongoStudyReviewRepositoryTest)
- **@Tag("ValidClasses")** for all nested classes that contains tests for the valid input values
- **@Tag("InvalidClasses")** for all nested classes that contains tests for the invalid input values

Notice that application services are unit tests as well as service test, so they receive both tags.

### Unit testing

**Value Objects**:  
- Cover all valid constructor equivalence classes using one or more test cases.
- Cover each invalid equivalence class with one test case each. 
- Cover all valid equivalence classes for each public method.
- Cover each invalid equivalence class of all public methods that receive one or more argument.

**Entities**: 
- Test all criteria above.
- Test all setter, add, remove methods to ensure they do not lead the object to an invalid state.

**Domain Services**
- Cover all valid equivalence classes of each public method using one or more test cases.
- Cover all invalid equivalence classes of each public method with one test case each.

**Application services**
- Cover the happy path and check if the presenter prepared a success view.
- Cover at least once the sad path tested by the PreconditionChecker class and check if the presenter prepared a 
fail view.
- Cover all additional sad paths to check if the presenter prepared a fail view

Guideline: **Do not implement or use fake classes**, use only mocks (@Mockk). Configure the mocks using *every{}* 
and check method invocation using *verify{}*.
Example: check the PreconditionCheckerTest class.

### Integration testing

**Database integration**

(Create a test class for each concrete implementation of a Repository interface)

- Test all database methods being used by the respective Repository interface with valid inputs.
- Test findOne queries that should return no result (should not throw).
- Cover all boundary values of the findAllBy methods (if applicable).
- Test findAll and FindAllBy queries that return no result. Should return empty list.

Guideline: Before each test, reset the database and the idGenerator. After each test, reset the database.

Example: check the MongoStudyReviewRepository class. 

**API integration**

(Create a test class for each rest controller)

- Cover the successful result of each endpoint of the controller. Assert the status code, the existence of the self 
link and the value of key response payload attributes.
- Cover all error result of each endpoint of the controller. Assert the error status code.

Guideline: Use the @SpringBootTest and the mockMvc to support the testing. 

Example: check the StudyReviewControllerTest class. 