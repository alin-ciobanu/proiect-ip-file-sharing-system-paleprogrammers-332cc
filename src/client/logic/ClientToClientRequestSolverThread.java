package client.logic;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientToClientRequestSolverThread extends Thread {

    Socket socket;

    public ClientToClientRequestSolverThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        ObjectInputStream objectInputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {

            ClientToClientRequest clientToClientRequest = null;

            try {
                clientToClientRequest = (ClientToClientRequest) objectInputStream.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            System.out.println("got req " + clientToClientRequest);

            if (clientToClientRequest.getCode() == ClientToClientRequest.TRANSFER) {

                String filename = clientToClientRequest.getFilename();
                File file = new File(filename);

                ClientToClientResponse response = new ClientToClientResponse();
                response.setFileLength(file.length());
                try {
                    response.setFile(FileUtils.readFileToByteArray(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    objectOutputStream.writeObject(response);
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
    }
}
