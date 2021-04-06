#Author: delaval-htps@gmail.com

@tag
Feature: While booting application, the file data.json has to be saved in databases
  As a administrator of SafetyNet , when I start the application, this last persists datas from file data.json 
  in the database to be able to use them for another operations.  

  @tag1
  Scenario: the Administrator starts application
    Given application SafetyNet is started and created databases
    When application read the data.json 
    Then the datas from this file are correctly persited in database
    
