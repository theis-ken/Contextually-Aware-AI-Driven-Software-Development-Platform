The bug in your FinanceTrackerTest.testPersistence is caused by Gson's inability to serialize/deserialize java.time.LocalDate out-of-the-box in Java 21. The error is:

com.google.gson.JsonIOException: Failed making field 'java.time.LocalDate#year' accessible; either increase its visibility or write a custom TypeAdapter for its declaring type.
...
Caused by: java.lang.reflect.InaccessibleObjectException: Unable to make field private final int java.time.LocalDate.year accessible: module java.base does not "opens java.time" to unnamed module

Solution:
You need to register a custom TypeAdapter for LocalDate with Gson. This adapter will convert LocalDate to/from a string (ISO format) during JSON serialization/deserialization.
