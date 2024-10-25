Online Pokemon Cards stor

By Moe Alasaadi

## Introduction

Hello and welcome!. this a demenstration for the Online pokemon cards store project.
I already established the connection to the UMD server. and i will start by running the server classs.

## Prerequisites

- Java Development Kit (JDK) installed
- Access to UMD server and VPN
- SQLite JDBC driver (sqlite-jdbc-3.46.1.3.jar)

## Running the Server

1. Navigate to the project server directory:

   cd '/home/m/malasaad/Online Pokemon Cards Server'

2. Compile all the project files in the package directory:

   javac -cp .:lib/sqlite-jdbc-3.46.1.3.jar src/main/java/org/example/*.java

3. Run the ServerMain class:

java -cp .:src/main/java:lib/sqlite-jdbc-3.46.1.3.jar org.example.ServerMain

You should see the following output:

Connection to SQLite has been established.
Tables created successfully.
Default user created with $100 balance
Server is running on port 6052

The server is now ready and waiting for client connections.

## Running the Client

1. Navigate to the ClientMain directory:

   cd '/home/m/malasaad/Online Pokemon Cards Client/src/main/java/org/example'

2. Compile ClientMain:

   javac ClientMain.java

3. Run ClientMain and specify the localhost:

java ClientMain.java localhost

You should see the following prompt:

Connected to server. Type commands or 'QUIT' to exit.
Client: Enter command:

The client is now ready to send commands to the server.

## Available Commands

- BUY: `BUY [card_name] [card_type] [rarity] [price] [quantity] [user_id]`
- SELL: `SELL [card_name] [quantity] [price] [user_id]`
- LIST: `LIST [user_id]`
- BALANCE: `BALANCE [user_id]`
- QUIT: Exit the client application
- SHUTDOWN: Shut down the server

## Example Usage

1. Buy a card:

   Client: BUY Pikachu Electric Common 19.99 2 1

   Now 

      BUY jojo Electric Uncommon 10.99 3 1


2. Sell a card:

   Client: SELL Pikachu 1 34.99 1
   Server: 200 OK
   SOLD: New balance: 1 Pikachu. User's balance USD $95.01

3. List cards:

   Client: LIST 1
   Server: 200 OK
   The list of records in the Pokemon cards table for current user, user 1:
   ID Card Name Card Type Rarity Count OwnerID
   1 Pikachu Electric Common 1 1

4. BALANCE Command:

   Client: Enter command: BALANCE 1
   Server: 200 OK
   Balance for user John Doe: $95.01

5. SHUTDOWN Command:

   Client: Enter command: SHUTDOWN
   Server: 200 OK
   Server shutting down...

6. QUIT Command:

   Client: Enter command: QUIT
   Server: 200 OK

## Troubleshooting

If you encounter any issues, please ensure:
- You are connected to the UMD server and VPN
- The SQLite JDBC driver is in the correct location
- The server is running before attempting to connect with the client

# Sample run for all commands 
## Client
[malasaad@login2:4 ~]$ cd '/home/m/malasaad/Online Pokemon Cards Client/src/main/java/org/example'
[malasaad@login2:4 ~/Online Pokemon Cards Client/src/main/java/org/example]$ javac ClientMain.java
[malasaad@login2:4 ~/Online Pokemon Cards Client/src/main/java/org/example]$ java ClientMain.java localhostConnected to server. Type commands or 'QUIT' to exit.
Client: Enter command: BUY Pikachu Electric Common 19.99 2 1
Server: 200 OK
BOUGHT: New balance: 2 Pikachu. User USD balance $60.02
Client: Enter command: BUY jojo Electric Uncommon 10.99 3 1
Server: 200 OK
BOUGHT: New balance: 3 jojo. User USD balance $27.05
Client: Enter command: BUY Pikachu Electric Common 19.99 2 22
Server: 404 User not found!
Client: Enter command: BUY Pikachu Electric Common 19.99 10 1
Server: 403 Not enough balance
Client: Enter command: LIST 1
Server: 200 OK
The list of records in the Pokemon cards table for current user, user 1:
ID    Card Name       Card Type       Rarity          Count      OwnerID
1     Pikachu         Electric        Common          2          1
2     jojo            Electric        Uncommon        3          1

Client: Enter command: BALANCE 1
Server: 200 OK
Balance for user Default User: $27.05
Client: Enter command: SELL Pikachu 1 34.99 1
Server: 200 OK
SOLD: New balance: 1 Pikachu. User's balance USD $62.04
Client: Enter command: LIST 1
Server: 200 OK
The list of records in the Pokemon cards table for current user, user 1:
ID    Card Name       Card Type       Rarity          Count      OwnerID
1     Pikachu         Electric        Common          1          1
2     jojo            Electric        Uncommon        3          1

Client: Enter command: BALANCE 1
Server: 200 OK
Balance for user Default User: $62.04
Client: Enter command: SELL Pikachu 1 34.99 33
Server: 404 User not found!
Client: Enter command: SELL Pikachu 10 34.99 1
Server: 403 Not enough cards to sell
Client: Enter command: LIST 20
Server: 404 User not found!
Client: Enter command: BALANCE 20
Server: 404 User not found!
Client: Enter command: BUY Pikachu Electric Common
Server: 403 Message format error
Client: Enter command:
Server: 400 Invalid command
Client: Enter command: SHUTDOWN
Server: 200 OK
Client: Enter command: QUIT
Client: 200 OK
[malasaad@login2:4 ~/Online Pokemon Cards Client/src/main/java/org/example]$

## Server
[malasaad@login2:5 ~]$ cd '/home/m/malasaad/Online Pokemon Cards Server'
[malasaad@login2:5 ~/Online Pokemon Cards Server]$ javac -cp .:lib/sqlite-jdbc-3.46.1.3.jar src/main/java/org/example/*.java
[malasaad@login2:5 ~/Online Pokemon Cards Server]$ java -cp .:src/main/java:lib/sqlite-jdbc-3.46.1.3.jar org.example.ServerMain
Connection to SQLite has been established.
Tables created successfully.
Default user created with $100 balance
Server is running on port 6052
New client connected
Received: BUY Pikachu Electric Common 19.99 2 1
Received: BUY jojo Electric Uncommon 10.99 3 1
Received: BUY Pikachu Electric Common 19.99 2 22
Received: BUY Pikachu Electric Common 19.99 10 1
Received: LIST 1
Received: BALANCE 1
Received: SELL Pikachu 1 34.99 1
Received: LIST 1
Received: BALANCE 1
Received: SELL Pikachu 1 34.99 33
Received: SELL Pikachu 10 34.99 1
Received: LIST 20
Received: BALANCE 20
Received: BUY Pikachu Electric Common
Received:
Received: SHUTDOWN
Server shutting down...
[malasaad@login2:5 ~/Online Pokemon Cards Server]$
