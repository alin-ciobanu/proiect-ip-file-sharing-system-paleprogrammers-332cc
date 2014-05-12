package client.logic;

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
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream ());
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

            if (clientToClientRequest.getCode() == ClientToClientRequest.TRANSFER) {
                /*
                TODO - Send requested file
                 */
            }

        }
    }
}
