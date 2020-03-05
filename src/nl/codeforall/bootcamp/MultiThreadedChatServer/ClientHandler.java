package nl.codeforall.bootcamp.MultiThreadedChatServer;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {

    private Socket client;
    private ChatServer chatServer;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public ClientHandler(Socket client, ChatServer chatServer) {
        this.client = client;
        this.chatServer = chatServer;
    }

    @Override
    public void run() {
        try {
            initialiseStreams();
            listenForMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialiseStreams() throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        printWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
    }

    public void listenForMessages() throws IOException {
        while (chatServer.isActive()) {
            String message = bufferedReader.readLine();

            if (message != null) {
                if (message.equals("logout")) {
                    closeAll();
                }
                chatServer.broadcast(message);
            }
        }
    }

    public void send(String message) {
        printWriter.println(Thread.currentThread().getName() + " says: " + message);
    }

    public void closeAll() throws IOException {
        try {
            chatServer.removeClient(this);
            client.close();
        } catch (SocketException e) {
            chatServer.broadcast(Thread.currentThread().getName() + " has logged out.");
        }
    }

}
