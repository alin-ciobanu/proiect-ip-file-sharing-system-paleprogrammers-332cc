package server.ui;

import server.logic.ClientTalkerThread;
import server.logic.User;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainFrame extends JFrame {

    MainPanel mainPanel;
    ServerSocket serverSocket;
    ArrayList<User> usersList;

    private final int PORT = 10001;

    public MainFrame () throws IOException {

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());

        mainPanel = new MainPanel();
        this.add(mainPanel);

        this.pack();
        this.setVisible(true);

        usersList = new ArrayList<User>();
        openListenSocket();

        while (true) {
            Socket socket = acceptClient();
            ClientTalkerThread clientTalkerThread = new ClientTalkerThread(socket, usersList);
            clientTalkerThread.start();
        }

    }

    public void openListenSocket () throws IOException {
        serverSocket = new ServerSocket(PORT);
    }

    public Socket acceptClient () throws IOException {
        return serverSocket.accept();
    }

    public static void main (String[] args) throws IOException {

        new MainFrame();

    }

}
