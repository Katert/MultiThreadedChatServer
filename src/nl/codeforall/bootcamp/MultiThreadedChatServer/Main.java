package nl.codeforall.bootcamp.MultiThreadedChatServer;

import nl.codeforall.bootcamp.MultiThreadedChatServer.ServerSide.ChatServer;

public class Main {

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }
}
