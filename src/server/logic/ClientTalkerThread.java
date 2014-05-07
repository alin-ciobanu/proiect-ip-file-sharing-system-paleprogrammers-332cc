package server.logic;

import client.logic.ClientRequest;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientTalkerThread extends Thread {

    Socket clientSocket;
    ArrayList<Socket> allSockets;

    public ClientTalkerThread (Socket clientSocket, ArrayList<Socket> allSockets) {

        this.clientSocket = clientSocket;
        this.allSockets = allSockets;

    }

    @Override
    public void run() {

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = clientSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream = clientSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (true) {

            ClientRequest request = null;
            try {
                request = (ClientRequest) objectInputStream.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (request.getRequestCode() == request.GET) {
                String file = request.getWhatFile();
                ServerResponse response = new ServerResponse();
                response.readFile(file);
                try {
                    objectOutputStream.writeObject(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (request.getRequestCode() == request.CLOSE) {
                allSockets.remove(clientSocket);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
