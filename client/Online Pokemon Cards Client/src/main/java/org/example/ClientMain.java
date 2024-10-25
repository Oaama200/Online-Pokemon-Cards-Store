package org.example;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientMain {
    // Define the server port number
    private static final int SERVER_PORT = 6052;

    public static void main(String[] args) throws IOException {
        // Check if the correct number of arguments is provided
        // in theis case it should be only one which is the server IP
        if (args.length != 1) {
            System.err.println("Usage: java ClientMain <server_ip>");
            System.exit(1); // Exit the program if the argument is missing
        }

        // Get the server IP address from the command line arguments
        String serverIp = args[0];

        // Try to establish a connection to the server
        try (Socket socket = new Socket(serverIp, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to server. Type commands or 'QUIT' to exit.");

            while (true) {
                try {
                    // Prompt the user to enter a command
                    System.out.print("Client: Enter command: ");
                    String userInput = scanner.nextLine();
                    // Check if the user wants to quit
                    if (userInput.equalsIgnoreCase("QUIT")) {
                        System.out.println("Client: 200 OK");
                        break;
                    }
                    // Send the user input to the server
                    out.println(userInput);
                    System.out.print("Server: ");
                    String response;
                    // Read the server's response
                    while (true) {
                        response = in.readLine();
                        if (response == null) {
                            throw new IOException("Server disconnected");
                        }
                        if (response.equals("/")) {
                            // Exit the loop if the end of response is signaled
                            break;
                        }
                        // Print the server's response
                        System.out.println(response);
                    }
                } catch (SocketException | EOFException e) {
                    System.err.println("Lost connection to server: " + e.getMessage());
                    break;
                } catch (IOException e) {
                    System.err.println("Error communicating with server: " + e.getMessage());
                    break;
                }
            }
        }
    }
}
mv C:\Users\muath\OneDrive\Desktop\CIS_427\Online Pokemon Cards Client
mv C:\Users\muath\OneDrive\Desktop\CIS_427\Online Pokemon Cards Server
