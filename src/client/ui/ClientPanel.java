package client.ui;

import client.logic.ClientRequest;
import org.apache.commons.io.FileUtils;
import server.logic.ServerResponse;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientPanel extends JPanel implements ActionListener {

    JLabel ipLabel = new JLabel("IP");
    JLabel portLabel = new JLabel("PORT");
    JLabel fileNameLabel = new JLabel("Filename");

    JTextField ipTextField = new JTextField();
    JTextField portTextField = new JTextField();
    JTextField fileNameTextField = new JTextField();

    JButton fileChooserButton = new JButton("Choose File");
    JFileChooser fileChooser = new JFileChooser();

    JPanel ipPanel = new JPanel();
    JPanel portPanel = new JPanel();
    JPanel fileNamePanel = new JPanel();

    JButton submitButton = new JButton("Get");

    String ipAddress;
    int port;
    String file;

    public ClientPanel () {

        ipPanel.setLayout(new BoxLayout(ipPanel, BoxLayout.X_AXIS));
        ipPanel.add(ipLabel);
        ipPanel.add(ipTextField);

        portPanel.setLayout(new BoxLayout(portPanel, BoxLayout.X_AXIS));
        portPanel.add(portLabel);
        portPanel.add(portTextField);

        fileNamePanel.setLayout(new BoxLayout(fileNamePanel, BoxLayout.X_AXIS));
        fileNamePanel.add(fileNameLabel);
        fileNamePanel.add(fileNameTextField);
        fileNamePanel.add(fileChooserButton);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(ipPanel);
        this.add(portPanel);
        this.add(fileNamePanel);
        this.add(submitButton);

        submitButton.addActionListener(this);
        fileChooserButton.addActionListener(this);

        this.setVisible(true);

    }

    private void connect () throws IOException, ClassNotFoundException {

        Socket socket = new Socket(ipAddress, port);
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());

        ClientRequest request = new ClientRequest();
        request.setRequestCode(request.GET);
        request.setWhatFile(file);
        objectOutputStream.writeObject(request);

        ServerResponse response = (ServerResponse) objectInputStream.readObject();
        byte[] bytes = response.getFileAsBytes();

        File outputFile = new File("C:\\out.dat");
        FileUtils.writeByteArrayToFile(outputFile, bytes, false);

        request = new ClientRequest();
        request.setRequestCode(request.CLOSE);
        objectOutputStream.writeObject(request);

        socket.close();

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if(actionEvent.getSource() == fileChooserButton)
        {
                int retVal = fileChooser.showOpenDialog(ClientPanel.this);
                if(retVal == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    fileNameTextField.setText(file.getAbsolutePath());
                }
        } else
        {
            ipAddress = ipTextField.getText();
            try{
                port = Integer.parseInt(portTextField.getText());
            } catch (NumberFormatException e){
                port = 10001;
                portTextField.setText("10001");
            }
            file = fileNameTextField.getText();

            try {
                connect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}
