database-jndi-connectors
========================

This project provide an example of a connector that use a database (tested with PostgreSQL) to store first and last name information and return the generated id of the newly inserted row.


Architecture
------------
Two layers are involved in this solution:
* Connector definition (`InsertQueryReturnsGeneratedIdConnector`) and implementation (`InsertQueryReturnsGeneratedIdConnectorImpl`) have been created in Bonita BPM Studio. Connector implementation doesn't contains any business logic and delegate everything to sub layers.
* A Java library (with a single class: `InsertQueryReturnsGeneratedId`)  has been created to received connector information (JNDI data source name, first name and last name). Library perform following operations:
  * Lookup of the data source
  * Get a connection from the data source
  * Prepare the query
  * Execute the query
  * Store the primary key value

Connector implementation act as a simple proxy to the Java library.


How to use this example
-----------------------
* Make sure you have a fully operationnal PostgreSQL server running
* Create a new user with username/password: test/test
* Run the database creation script (`create-db`) that you can find under `src/test/resources`
* Add PostgreSQL JDBC driver to <TOMCAT_HOME>/lib
* Delcare the data source in `bonita.xml` file (see example below)
* Build the library using Maven: `mvn clean install`
* Open Bonita BPM Studio (6.3.8 or later), go to `Development -> Manage jars...` and import the jar file you just built (located in `target` folder)
* Click on Studio `Import` button and import `src/test/resources/DatabaseCustomConnectorDiagram-1.0.bos`. This file include connector definition and implementation. The implementation refer to the jar file you just built and import in the Studio.
* Now should be able to run the process from the Studio.


Data source declaration example
-------------------------------
Following configuration should be added to <TOMCAT_HOME>/conf/Catalina/localhost/bonita.xml under <context> tag:

      <Resource name="testDS"
              auth="Container"
              type="javax.sql.DataSource"
              maxActive="5"
              minIdle="1"
              maxWait="10000"
              initialSize="1"
              validationQuery="SELECT 1"
              validationInterval="30000"
              removeAbandoned="true"
              logAbandoned="true"
              username="test"
              password="test"
              driverClassName="org.postgresql.Driver"
              url="jdbc:postgresql://localhost:5432/testdb"/>
              
Troubleshooting
---------------
If you get any error while trying to run the process, check the Engine log file available in `Help -> Bonita BPM Engine log` Studio menu.
