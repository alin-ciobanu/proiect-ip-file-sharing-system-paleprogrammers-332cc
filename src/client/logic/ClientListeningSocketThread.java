package client.logic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientListeningSocketThread extends Thread {

    private static volatile ClientListeningSocketThread instance = null;
    ServerSocket serverSocket;

    private ClientListeningSocketThread(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public static ClientListeningSocketThread getInstance (ServerSocket serverSocket) {
        if (instance == null) {
            synchronized (ClientListeningSocketThread.class) {
                if (instance == null) {
                    instance = new ClientListeningSocketThread(serverSocket);
                }
            }
        }
        return instance;
    }

    @Override
    public void run() {

        while (true) {

            Socket socket;

            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
