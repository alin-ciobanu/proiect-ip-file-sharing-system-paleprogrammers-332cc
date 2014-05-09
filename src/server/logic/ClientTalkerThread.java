package server.logic;

import client.logic.ClientRequest;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientTalkerThread extends Thread {

    Socket clientSocket;
    ArrayList<User> usersList;

    public ClientTalkerThread (Socket clientSocket, ArrayList<User> usersList) {

        this.clientSocket = clientSocket;
        this.usersList = usersList;

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

            int reqCode = request.getRequestCode();

            if (reqCode == request.REGISTER) {

                User user = new User();
                user.setAlias(request.getUsername());
                user.setSharedTree(request.getFileTreeToShare());
                user.setSocket(clientSocket);
                usersList.add(user);

                ServerResponse serverResponse = new ServerResponse();
                serverResponse.setUsersList(usersList);
                try {
                    objectOutputStream.writeObject(serverResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            else if (request.getRequestCode() == request.CLOSE) {
                User userBySocket = getUserBySocket();
                usersList.remove(userBySocket);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private User getUserBySocket () {
        for (User user : usersList) {
            if (user.getSocket().equals(clientSocket)) {
                return user;
            }
        }
        return null;
    }

}
