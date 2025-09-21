Feature: Serialize Companies to flat cobol file

Scenario: Serialize companies and their employees to a Dat file 
   Given companies datasets
   When I run the "serializeCompanies" job
   Then genrerated file "companies.dat" must be identical to file "/output/expected/companies.dat"