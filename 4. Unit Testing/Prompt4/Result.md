For Unit testing, Cursor AI suggested JUnit 5.
It immediately change the dependencies and created sample tests for each class and isolated methods for each method.
Each class created in response to this prompt, is has a comment containing "Prompt 4".

All the Unit tests can be found under "Coding\src\test\java\com\example", except for "FinanceTrackerIntegrationTest", which is part of the last section, Integration testing. 

Results of the tests:

Running com.example.CategoryTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.031 s -- in com.example.CategoryTest
[INFO] Running com.example.FinanceSummaryTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 s -- in com.example.FinanceSummaryTest
[INFO] Running com.example.FinanceTrackerTest
[INFO] Warning: 80\% of spending limit reached for category: Groceries
[ERROR] Tests run: 6, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.059 s <<< FAILURE! -- in com.example.FinanceTrackerTest
[ERROR] com.example.FinanceTrackerTest.testPersistence -- Time elapsed: 0.047 s <<< ERROR!
com.google.gson.JsonIOException: Failed making field 'java.time.LocalDate#year' accessible; either increase its visibility or write a custom TypeAdapter for its declaring type.
[INFO] Running com.example.SpendingLimitTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 s -- in com.example.SpendingLimitTest
[INFO] Running com.example.TransactionTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 s -- in com.example.TransactionTest

[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0