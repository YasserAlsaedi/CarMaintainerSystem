package org.example;


import org.example.util.Protocol;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MainClient {

    private static final String LOG_FILE = "client_log.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("================================================");
        System.out.println("  Car Maintenance Reservation System - Client");
        System.out.println("================================================");
        System.out.println("Connecting to server at " + Protocol.HOST + ":" + Protocol.PORT + "...");

        // IO Streams — open a log file to record all actions (backup)
        try (Socket socket = new Socket(Protocol.HOST, Protocol.PORT);
             BufferedReader serverIn  = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter   serverOut  = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream()), true);
             PrintWriter   logWriter  = new PrintWriter(
                     new FileWriter(LOG_FILE, true), true)) {

            System.out.println("Connected!\n");
            printMenu();

            while (true) {
                System.out.print("\nEnter command (or 'HELP'): ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) continue;

                if (input.equalsIgnoreCase("HELP")) {
                    printMenu();
                    continue;
                }

                // Send command to server
                serverOut.println(input);

                // Log the command locally (IO Streams)
                logWriter.println("[SENT] " + input);

                // Read server response
               // String response = serverIn.readLine();
               // System.out.println("\n[Server Response]\n" + response.replace("|", " "));
                StringBuilder fullResponse = new StringBuilder();
                String line;
                // Read all available lines from server
                while (serverIn.ready() || fullResponse.length() == 0) {
                    line = serverIn.readLine();
                    if (line == null) break;
                    fullResponse.append(line).append("\n");
                    if (!serverIn.ready()) break;
                }
                System.out.println("\n[Server Response]\n" + fullResponse.toString().replace("|", " "));

                // Log the response
                //logWriter.println("[RECV] " + response);
                logWriter.println("[RECV] " + fullResponse.toString());
                logWriter.println("---");

                // Exit if done
                if (input.equalsIgnoreCase(Protocol.EXIT)) {
                    System.out.println("Disconnected. Log saved to: " + LOG_FILE);
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("[Client] Connection error: " + e.getMessage());
            System.err.println("Make sure the server is running!");
        }
    }

    private static void printMenu() {
        System.out.println("""
                ┌──────────────────────────────────────────────────────────┐
                │           Available Commands                             │
                ├──────────────────────────────────────────────────────────┤
                │ ADD_CUSTOMER|name|phone|email                            │
                │ REMOVE_CUSTOMER|customerId                               │
                │ ADD_CAR|customerId|make|model|plateNumber                │
                │ BOOK_APPOINTMENT|carId|date(YYYY-MM-DD HH:MM)|desc      │
                │ UPDATE_STATUS|appointmentId|Pending/In Progress/Done    │
                │ VIEW_APPOINTMENTS                                        │
                │ VIEW_CUSTOMERS                                           │
                │ VIEW_CARS                                                │
                │ EXIT                                                     │
                └──────────────────────────────────────────────────────────┘
                Examples:
                  ADD_CUSTOMER|Ali Hassan|0501112233|ali@email.com
                  ADD_CAR|1|Toyota|Camry|ABC-9999
                  BOOK_APPOINTMENT|1|2025-06-10 09:00|Oil Change
                  UPDATE_STATUS|1|In Progress
                """);
    }
}
