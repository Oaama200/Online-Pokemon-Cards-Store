package org.example;

import java.io.*;
import java.net.*;


public class ServerMain {
    // Define the server port number
    private static final int SERVER_PORT = 6052;
    private static final String FORMAT_ERROR = "403 Message format error";
    // Declare a DatabaseManager instance
    private static DatabaseManager dbManager;

    public static void main(String[] args) throws ClassNotFoundException {
        // Load the SQLite JDBC driver
        Class.forName("org.sqlite.JDBC");
        dbManager = new DatabaseManager();
        dbManager.createTables();
        // Uncomment the following line to insert sample data into the database
        //dbManager.insertSampleData();

        // Check if there are any users, if not create a default one
        if (!dbManager.hasUsers()) {
            dbManager.createDefaultUser();
        }
        // Start the server and listen on the specified port
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server is running on port " + SERVER_PORT);

            while (true) {
                // Accept a new client connection
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("New client connected");

                    String inputLine;
                    // Read input from the client
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("Received: " + inputLine);
                        // Process the received command and generate a response
                        String response = processCommand(inputLine);
                        // Send the response back to the client
                        out.println(response);
                        out.println("/"); // Send "/" to signal end of response

                        // Shutdown the server if the SHUTDOWN command is received
                        if (inputLine.equalsIgnoreCase("SHUTDOWN")) {
                            System.out.println("Server shutting down...");
                            // Close the database connection and socket
                            dbManager.closeConnection();
                            clientSocket.close();
                            return;
                        }
                    }
                } catch (IOException e) {
                    // Handle exceptions related to client communication
                    System.err.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            // Handle exceptions related to server socket
            System.err.println("Could not listen on port " + SERVER_PORT);
            System.exit(-1);
        }
    }

    // Process commands received from the client
    private static String processCommand(String command) {
        if (command == null || command.isBlank() || command.trim().isEmpty()){
            return "400 Invalid command";
        }

        String[] parts = command.split(" ");
        switch (parts[0].toUpperCase()) {
            case "BUY":
                return handleBuy(parts);
            case "SELL":
                return handleSell(parts);
            case "LIST":
                return handleList(parts);
            case "BALANCE":
                return handleBalance(parts);
            case "SHUTDOWN":
            case "QUIT":
                return "200 OK";
            default:
                return "400 Invalid command";
        }
    }

    // Handle the BUY command
    private static String handleBuy(String[] parts) {
        if (parts.length != 7) {
            return FORMAT_ERROR;
        }
        try {
            String cardName = parts[1];
            String cardType = parts[2];
            String rarity = parts[3];
            double price = Double.parseDouble(parts[4]);
            int count = Integer.parseInt(parts[5]);
            int ownerId = Integer.parseInt(parts[6]);
            return dbManager.buyCard(cardName, cardType, rarity, price, count, ownerId);
        } catch (NumberFormatException e) {
            return FORMAT_ERROR;
        }
    }

    // Handle the SELL command
    private static String handleSell(String[] parts) {
        if (parts.length != 5) {
            return FORMAT_ERROR;
        }
        try {
            String cardName = parts[1];
            int quantity = Integer.parseInt(parts[2]);
            double price = Double.parseDouble(parts[3]);
            int ownerId = Integer.parseInt(parts[4]);
            return dbManager.sellCard(cardName, quantity, price, ownerId);
        } catch (NumberFormatException e) {
            return FORMAT_ERROR;
        }
    }

    // Handle the LIST command
    private static String handleList(String[] parts) {
        if (parts.length != 2) {
            return FORMAT_ERROR;
        }
        try {
            int ownerId = Integer.parseInt(parts[1]);
            return dbManager.listCards(ownerId);
        } catch (NumberFormatException e) {
            return FORMAT_ERROR;
        }
    }

    // Handle the BALANCE command
    private static String handleBalance(String[] parts) {
        if (parts.length != 2) {
            return FORMAT_ERROR;
        }
        try {
            int ownerId = Integer.parseInt(parts[1]);
            return dbManager.getBalanceForUser(ownerId);
        } catch (NumberFormatException e) {
            return FORMAT_ERROR;
        }
    }
}