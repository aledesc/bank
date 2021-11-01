1- Database
--------------------------------------------------
Data of Users and Accounts are stored in a graph database in Neo4J Aura.

To inspect current state of the data the following credentials  can be used in Neo4J Browser / Desktop

    url: neo4j+s://332d98e8.databases.neo4j.io
    usr: neo4j
    pas: GAiulrJnaeq5HD8OjfAKSFqxJI97rAd30eg6S3xCrko
     db: neo4j

At https://neo4j.com/product/developer-tools/ can be downloaded the Neo4j Desktop to connect to the database, and the Browser, to explore and visualize the graph.

2- Building the jar
------------------------------
Cd to the root directory and issue a: 

        gradle build

This will create a bank-1.0.jar under /build/libs


3- To Run the jar
--------------------------
The solution is Java SDK 11 compatible, so it must be installed on the running SO.

To Run the application, cd /buid/libs and issue a: 
        
        java -jar bank-1.0.jar

By default, the app listen on port 8080, to change it, in the /resources/application.properties file, update server.port property

4- Registering users, creating accounts and moving your money
-------------------------------------------------------------
It all can be done using Postman. On the root dir of the app there are a couple of files, one for the Postman Environment (Local.postman_environment.json), another for the Collections (bank api.postman_collection) to be imported to Postman

The different request in the collections are: 

- register:         to register a new user
- login:            to login as a user & get the jwt token
- create account:   to create an account for a user with an initial amount of money 
- deposit:          to deposit money on a users account
- withdraw:         to withdraw money from a users account
- list movements:   to list the movement on that users account, there is some code to visualize the movements and the account's balance resulting                                       after each movement, on tap Visualize of Postman there is a neat table

Using Neo4j Desktop the evolution of the account can be visualized in a nice way ! 

 

