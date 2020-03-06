package nl.codeforall.bootcamp.MultiThreadedChatServer.ServerSide;

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
            System.out.print("\nConfigure port number: ");
            int portNumber = input.nextInt();
            serverSocket = new ServerSocket(portNumber);

            ExecutorService threadPool = Executors.newCachedThreadPool();
            clients = new LinkedList<>();
            InetAddress ipAddress = InetAddress.getLocalHost();
            System.out.println("Chat server running, clients can connect to port " + portNumber + " (IP: " + ipAddress.toString() + ").\n");

            // Listen for connections
            while (!serverSocket.isClosed()) {
                try {
                    String randomClientName = ("User-" + (int) (Math.random() * 1000));
                    Socket clientSocket = serverSocket.accept();
                    System.out.println(randomClientName + " connected to the chat room (" + new Date() + ").");
                    ClientHandler client = new ClientHandler(randomClientName, clientSocket, this);
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

    public void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    public boolean isActive() {
        return !serverSocket.isClosed();
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    public StringBuilder activeClients() {
        StringBuilder list = new StringBuilder("Clients logged in: \n");
        for (ClientHandler client : clients) {
            list.append(client.getName()).append("\n");
        }
        return list;
    }

    public List<ClientHandler> getClientList() {
        return clients;
    }

}
