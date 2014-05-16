package client.logic;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;

public class ClientToClientRequestSenderThread extends Thread {

    Socket socket;
    String pathToFile;
    String localPathToFile;

    public String getLocalPathToFile() {
        return localPathToFile;
    }

    @Override
    public String toString() {
        return "ClientToClientRequestSenderThread{" +
                "socket=" + socket +
                ", pathToFile='" + pathToFile + '\'' +
                ", localPathToFile='" + localPathToFile + '\'' +
                '}';
    }

    public void setLocalPathToFile(String localPathToFile) {
        this.localPathToFile = localPathToFile;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    @Override
    public void run() {

        InputStream in = null;
        OutputStream out = null;

        try {
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectInputStream inputStream = null;
        ObjectOutputStream outputStream = null;

        try {
            outputStream = new ObjectOutputStream(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream = new ObjectInputStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ClientToClientRequest clientRequest = new ClientToClientRequest();
        clientRequest.setCode(ClientToClientRequest.TRANSFER);
        clientRequest.setFilename(pathToFile);

        try {
            outputStream.writeObject(clientRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientToClientResponse response = null;
        try {
            response = (ClientToClientResponse) inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        long fileLen = response.getFileLength();
        try {
            FileUtils.writeByteArrayToFile(new File(localPathToFile), response.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
