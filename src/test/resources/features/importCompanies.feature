Feature: Import Companies from JSON

  Scenario: Import companies and their employees from a JSON file
    Given the JSON file "./src/test/resources/jsons/companies.json"
    When I run the "importCompanies" job
    Then the job should end with status "COMPLETED"
    And the following companies should be registered:
      | id       | name       | location      | industry  |
      | compA123 | TechNova   | Paris, France | Software  |
      | compB456 | GreenLogix | Lyon, France  | Logistics |
    And the following employees should be registered:
      | id      | name             | position              | email                           | companyId |
      | emp001X | Alice Dupont     | Software Engineer     | alice.dupont@technova.com       | compA123  |
      | emp002Y | Marc Lemoine     | Product Manager       | marc.lemoine@technova.com       | compA123  |
      | emp003Z | Claire Martin    | Logistics Coordinator | claire.martin@greenlogix.com    | compB456  |
      | emp004W | Youssef Belkacem | Operations Analyst    | youssef.belkacem@greenlogix.com | compB456  |

  Scenario: Importing an empty list of companies
    Given the JSON file "./src/test/resources/jsons/companies_empty.json"
    When I run the "importCompanies" job
    Then the job should end with status "COMPLETED"
    And the following companies should be registered:
      | id | name | location | industry |
    And the following employees should be registered:
      | id | name | position | email | companyId |
