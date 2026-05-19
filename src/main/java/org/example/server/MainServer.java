package org.example.server;

import org.example.util.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {

    private static final int MAX_THREADS = 10;

    public static void main(String[] args) {
        // Thread pool: handles up to MAX_THREADS clients concurrently
        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);

        System.out.println("==============================================");
        System.out.println("  Car Maintenance Server starting...");
        System.out.println("  Listening on port " + Protocol.PORT);
        System.out.println("==============================================");

        int clientCounter = 0;

        try (ServerSocket serverSocket = new ServerSocket(Protocol.PORT)) {

            while (true) {
                // Blocking call — waits for a client to connect
                Socket clientSocket = serverSocket.accept();
                clientCounter++;

                // Spawn a new thread from the pool to handle this client
                ClientHandler handler = new ClientHandler(clientSocket, clientCounter);
                threadPool.execute(handler);
            }

        } catch (IOException e) {
            System.err.println("[Server] Fatal error: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }
}
