#Author: delaval-htps@gmail.com

@tag
Feature: While booting application, the file data.json has to be saved in databases
  As a administrator of SafetyNet , when I start the application, this last persists datas from file data.json 
  in the database to be able to use them for another operations.  

  @tag1
  Scenario: administrator starts application -- data file is persisted
    Given application starts and creates databases
    And data file contains this first person:
    |firstName	|lastName	|address 				|city		|zip		|phone				|email						|
    |John				|Boyd			|1509 Culver St	|Culver	|97451	|841-874-6512	|jaboyd@email.com	|
    When application reads the data.json 
    Then the datas from this file are correctly persited in database
    
