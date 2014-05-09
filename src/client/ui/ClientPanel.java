package client.ui;

import client.logic.ClientRequest;
import server.logic.ServerResponse;
import server.logic.User;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientPanel extends JPanel implements ActionListener {

    Socket socketToServer;
    ObjectOutputStream objectOutputStreamToServer;
    ObjectInputStream objectInputStreamToServer;
    ArrayList<User> usersList;


    JTree fileTree = new JTree(new DefaultMutableTreeNode());
    JTree selectedFileTree = new JTree(new DefaultMutableTreeNode());

    JLabel aliasLabel = new JLabel("ALIAS");
    JLabel ipLabel = new JLabel("IP");
    JLabel portLabel = new JLabel("PORT");
    JLabel usernameLabel = new JLabel("Alias");

    JTextField aliasTextField = new JTextField();
    JTextField ipTextField = new JTextField();
    JTextField portTextField = new JTextField();
    JTextField usernameTextField = new JTextField();

    JPanel inputPanel = new JPanel();
    JPanel aliasPanel = new JPanel();
    JPanel ipPanel = new JPanel();
    JPanel portPanel = new JPanel();
    JPanel usernamePanel = new JPanel();
    JPanel fileTreePanel = new JPanel();
    JPanel selectedTreePanel = new JPanel();
    JSplitPane treesPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,fileTreePanel,selectedTreePanel);

    JScrollPane fileTreePane = new JScrollPane();
    JScrollPane selectedTreePane = new JScrollPane();

    JButton submitButton = new JButton("Register");
    JButton fileChooserButton = new JButton("Choose File");
    JFileChooser fileChooser = new JFileChooser();

    String ipAddress;
    int port;
    String username;

    public ClientPanel () {
        aliasPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE,10));
        aliasPanel.setLayout(new BoxLayout(aliasPanel, BoxLayout.X_AXIS));
        aliasPanel.add(aliasLabel);
        aliasPanel.add(aliasTextField);

        ipPanel.setLayout(new BoxLayout(ipPanel, BoxLayout.X_AXIS));
        ipPanel.add(ipLabel);
        ipPanel.add(ipTextField);

        portPanel.setLayout(new BoxLayout(portPanel, BoxLayout.X_AXIS));
        portPanel.add(portLabel);
        portPanel.add(portTextField);

        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);
        usernamePanel.add(fileChooserButton);
        usernameTextField.setPreferredSize(new Dimension(100,10));

        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        inputPanel.add(ipPanel);
        inputPanel.add(portPanel);
        inputPanel.add(usernamePanel);
        inputPanel.add(submitButton);

        this.add(inputPanel);

        fileTreePane.getViewport().add(fileTree);
        fileTreePanel.add(fileTreePane);
        fileTreePanel.setPreferredSize(new Dimension(150, 400));
        fileTreePane.setPreferredSize(new Dimension(150, 400));
        //treesPanel.add(fileTreePanel);

        selectedTreePane.getViewport().add(selectedFileTree);
        selectedTreePanel.add(selectedTreePane);
        selectedTreePanel.setPreferredSize(new Dimension(150, 400));
        selectedTreePane.setPreferredSize(new Dimension(150, 400));
        //treesPanel.add(selectedTreePanel);
        //treesPanel.setLayout(new GridLayout(1,2));
        this.add(treesPanel);
        submitButton.addActionListener(this);
        fileChooserButton.addActionListener(this);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        this.setVisible(true);

    }

    private void connectToServer () throws IOException, ClassNotFoundException {

        socketToServer = new Socket(ipAddress, port);
        objectOutputStreamToServer = null;
        objectInputStreamToServer = null;
        objectOutputStreamToServer = new ObjectOutputStream(socketToServer.getOutputStream());
        objectInputStreamToServer = new ObjectInputStream(socketToServer.getInputStream());

        ClientRequest request = new ClientRequest();
        request.setRequestCode(request.REGISTER);
        request.setUsername(username);
        request.setFileTreeToShare((DefaultMutableTreeNode)selectedFileTree.getModel().getRoot());
        objectOutputStreamToServer.writeObject(request);

        ServerResponse serverResponse = (ServerResponse) objectInputStreamToServer.readObject();
        usersList = serverResponse.getUsersList();

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if(actionEvent.getSource() == fileChooserButton)
        {
                int retVal = fileChooser.showOpenDialog(ClientPanel.this);
                if(retVal == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    fileTreePane.getViewport().remove(fileTree);

                    fileTree = new JTree(makeTree(null, file));
                    fileTree.addTreeSelectionListener(new TreeSelectionListener() {
                        @Override
                        public void valueChanged(TreeSelectionEvent e) {
                            System.out.println(e.getPath().toString());
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode)fileTree.getLastSelectedPathComponent();
                            DefaultMutableTreeNode sourceTree = (DefaultMutableTreeNode)fileTree.getModel().getRoot();
                            selectedFileTree = new JTree((DefaultMutableTreeNode)copySubTree(node,sourceTree));
                            selectedTreePane.getViewport().add(selectedFileTree);
                        }
                    });
                    fileTreePane.getViewport().add(fileTree);
                    this.revalidate();
                    this.repaint();
                }
        } else if (actionEvent.getSource() == submitButton)
        {
            ipAddress = ipTextField.getText();
            try{
                port = Integer.parseInt(portTextField.getText());
            } catch (NumberFormatException e){
                port = 0;
                portTextField.setText("0");
            }
            username = usernameTextField.getText();

            try {
                connectToServer();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    DefaultMutableTreeNode makeTree(DefaultMutableTreeNode root, File file)
    {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file.getName());

        if(root == null){
            newNode = new DefaultMutableTreeNode(file.getPath());
        } else {
            root.add(newNode);
        }

        ArrayList<File> files = new ArrayList<File>(Arrays.asList(file.listFiles()));
        for(File child : files){
            if(child.isDirectory()){
                makeTree(newNode,child);
            } else {
                newNode.add(new DefaultMutableTreeNode(child.getName()));
            }
        }
        return newNode;
    }

    public DefaultMutableTreeNode copySubTree(DefaultMutableTreeNode subRoot, DefaultMutableTreeNode sourceTree)
    {
        if (sourceTree == null)
        {
            return subRoot;
        }
        for (int i = 0; i < sourceTree.getChildCount(); i++)
        {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)sourceTree.getChildAt(i);
            DefaultMutableTreeNode clone = new DefaultMutableTreeNode(child.getUserObject());

            copySubTree(clone, child);
        }
        return subRoot;
    }
}
