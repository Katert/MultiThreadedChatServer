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

                switch (message.split(" ")[0]) {
                    case "/shout":
                        messageUppercased(message.substring(6));
                        break;
                    case "/logout":
                        closeAll();
                        return;
                    case "/name":
                        changeName(message.substring(6));
                        break;
                    default:
                        chatServer.broadcastMessage(message);
                        break;
                }
            }

            if (message == null) {
                chatServer.removeClient(this);
                closeAll();
            }

        }
    }

    public void sendMessage(String message) {
        printWriter.println(Thread.currentThread().getName() + " says: " + message);
    }

    public void sendChange(String change) {
        printWriter.println(change);
    }

    public void closeAll() throws IOException {
        try {
            chatServer.broadcastChange(Thread.currentThread().getName() + " has logged out.");
            chatServer.removeClient(this);
            client.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }

    public void changeName(String name) throws IOException {
        String currentName = Thread.currentThread().getName();
        chatServer.broadcastChange(currentName + " changed name into " + name + ".");
        Thread.currentThread().setName(name);
    }

    public void messageUppercased(String message) throws IOException {
        chatServer.broadcastMessage(message.toUpperCase());
    }

}
