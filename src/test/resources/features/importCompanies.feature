Feature: Import Companies from flat cobol file

  Scenario: Import companies and their employees from a Dat file
    Given the Dat file "companies.dat"
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
    Given the Dat file "companies_empty.dat"
    When I run the "importCompanies" job
    Then the job should end with status "COMPLETED"
    And the following companies should be registered:
      | id | name | location | industry |
    And the following employees should be registered:
      | id | name | position | email | companyId |

  Scenario: Split large Dat file into smaller chunks
    Given the Dat file "companies_100K.dat"
    And the split file max size is "10240" bytes
    When I run the "importCompanies" job
    Then the directory "./src/test/resources/dats/split/" should contain multiple split Dat files
    And each split file should be smaller than "10840" bytes
    And exactly 200 companies should be present in the database
    And exactly 396 employees should be registered in the database

  Scenario Outline: Should terminate with FAILED status when <file> is inconsistent
    Given the Dat file "<file>"
    When I run the "importCompanies" job
    Then the job should end with status "FAILED"
  Examples:
    | file                |
    | orphan_employee.dat |
    | no_employee.dat     |

 Scenario: Should split file when size exceed at the middle of company block
    Given the Dat file "companies.dat"
    And the split file max size is "600" bytes
    When I run the "importCompanies" job
    Then the job should end with status "COMPLETED"
    And the directory "./src/test/resources/dats/split/" should contain multiple split Dat files
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

