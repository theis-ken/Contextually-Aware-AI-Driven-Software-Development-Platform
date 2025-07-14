Given the main components in the finance tracker, I will suggest some integration tests to validate interactions between them. 
In the first one I want to ensure that when an expense transaction is added in a category with a spending limit, the limit is updated correctly. 
The second one, add multiple transactions and categories, save files, reload from file, and check that data is accurately resorted and still linked properly.
Third, When a category is deleted, it should be removed from all transactions and associated spending limits.
These are the 3 integration test I am planning. Can you assist me in implementing these and suggest additional meaningful integration tests?