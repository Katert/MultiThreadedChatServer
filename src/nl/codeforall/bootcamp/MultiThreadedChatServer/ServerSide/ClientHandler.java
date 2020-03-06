package nl.codeforall.bootcamp.MultiThreadedChatServer.ServerSide;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {

    private Socket client;
    private ChatServer chatServer;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String name;

    public ClientHandler(String name, Socket client, ChatServer chatServer) {
        this.client = client;
        this.chatServer = chatServer;
        this.name = name;
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

    private void initialiseStreams() throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        printWriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
    }

    private void listenForMessages() throws IOException {
        while (chatServer.isActive()) {

            String message = bufferedReader.readLine();

            if (message != null) {

                switch (message.split(" ")[0]) {
                    case "/whisper":
                        whisperMessage(message);
                        break;
                    case "/shout":
                        messageUppercased(message.substring(6));
                        break;
                    case "/logout":
                        closeAll();
                        return;
                    case "/name":
                        changeName(message.substring(6));
                        break;
                    case "/list":
                        sendMessage(chatServer.activeClients().toString());
                        break;
                    default:
                        chatServer.broadcast(name + " says: " + message);
                        break;
                }
            }
        }
    }

    public void sendMessage(String message) {
        printWriter.println(message);
    }

    private void closeAll() throws IOException {
        try {
            chatServer.broadcast(name + " has logged out.");
            chatServer.removeClient(this);
            client.close();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }

    private void whisperMessage(String message) {
        String recipient = message.split(" ")[1];

        for (ClientHandler client : chatServer.getClientList()) {
            if (client.getName().equals(recipient)) {
                String[] messageArray = message.split(" ");
                String whisperedMessage = "";
                int messageIndex = 2;
                for (int i = messageIndex; i < messageArray.length; i++) {
                    whisperedMessage += messageArray[i] + " ";
                }
                client.sendMessage(name + " whispers: " + whisperedMessage);
                sendMessage("You whispered to " + name + ": " + (message = whisperedMessage));
            }
        }
    }

    private void changeName(String name) {

        if (this.name.equals(name)) {
            sendMessage("You are already using this name.");
            return;
        }

        chatServer.broadcast(this.name + " changed name into " + name + ".");
        this.name = name;
    }

    private void messageUppercased(String message) {
        chatServer.broadcast(message.toUpperCase());
    }

    public String getName() {
        return name;
    }

}
