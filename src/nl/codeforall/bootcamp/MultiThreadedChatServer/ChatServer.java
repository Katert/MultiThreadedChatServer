package nl.codeforall.bootcamp.MultiThreadedChatServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private Scanner input = new Scanner(System.in);
    private List<ClientHandler> clients;
    private ServerSocket serverSocket;

    public void start() {

        try {

            // Create server socket
            System.out.print("Configure port number: ");
            int portNumber = input.nextInt();
            serverSocket = new ServerSocket(portNumber);

            ExecutorService threadPool = Executors.newCachedThreadPool();
            clients = new LinkedList<>();
            InetAddress ipAddress = InetAddress.getLocalHost();
            System.out.println("Chat server running, clients can connect to port " + portNumber + " (IP: " + ipAddress.toString() + ").\n");

            // Listen for connections
            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[USER] connected to chat room (" + new Date() + ").");
                    ClientHandler client = new ClientHandler(clientSocket, this);
                    threadPool.submit(client);
                    clients.add(client);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void broadcastMessage(String message) throws IOException {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void broadcastChange(String change){
        for (ClientHandler client : clients) {
            client.sendChange(change);
        }
    }

    public boolean isActive() {
        return !serverSocket.isClosed();
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }


}
