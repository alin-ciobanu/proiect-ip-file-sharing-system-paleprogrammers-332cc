package client.ui;

import client.logic.ClientListeningSocketThread;
import client.logic.ClientRequest;
import client.logic.ClientToClientRequestSenderThread;
import server.logic.ServerResponse;
import server.logic.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientPanel extends JPanel implements ActionListener {

    private static final String SAVE_FOLDER = "D:\\IP\\Downloads\\";

    Socket socketToServer;
    ServerSocket listenSocket;
    ObjectOutputStream objectOutputStreamToServer;
    ObjectInputStream objectInputStreamToServer;
    ArrayList<User> usersListSource = new ArrayList<User>();

    JTree fileTree = new JTree(new DefaultMutableTreeNode());
    JTree selectedFileTree = new JTree(new DefaultMutableTreeNode("\\"));
    JTree downloadedFilesTRee = new JTree(new DefaultMutableTreeNode());

    JLabel aliasLabel = new JLabel("ALIAS");
    JLabel ipLabel = new JLabel("IP");
    JLabel portLabel = new JLabel("PORT");
    JLabel usernameLabel = new JLabel("Alias");
    JLabel listeningPortJLabel = new JLabel("Listening port");

    JTextField aliasTextField = new JTextField();
    JTextField ipTextField = new JTextField();
    JTextField portTextField = new JTextField();
    JTextField usernameTextField = new JTextField();
    JTextField listeningPortJTextField = new JTextField();

    JPanel inputPanel = new JPanel();
    JPanel aliasPanel = new JPanel();
    JPanel ipPanel = new JPanel();
    JPanel portPanel = new JPanel();
    JPanel usernamePanel = new JPanel();
    JPanel listeningPortPanel = new JPanel();
    JPanel fileTreePanel = new JPanel();
    JPanel selectedTreePanel = new JPanel();

    JPanel usersListPanel = new JPanel();
    JPanel usersFilesPanel = new JPanel();

    JList<String> usersList = new JList<String>();

    JTabbedPane managementPane = new JTabbedPane();
    JScrollPane fileTreePane = new JScrollPane();
    JScrollPane selectedTreePane = new JScrollPane();
    JScrollPane usersListScrollPane = new JScrollPane();
    JScrollPane usersFilesScrollPane = new JScrollPane();

    JSplitPane treesPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,fileTreePanel,selectedTreePanel);
    JSplitPane usersPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,usersListPanel,usersFilesPanel);

    JButton submitButton = new JButton("Register");
    JButton fileChooserButton = new JButton("Choose File");
    JButton addFileButton = new JButton("Add File");
    JButton removeFileButton = new JButton("Remove File");
    JButton downloadButton = new JButton("Download");
    JFileChooser fileChooser = new JFileChooser();

    String ipAddress;
    int port;
    String username;
    Boolean changedDirectory = true;

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

        listeningPortPanel.setLayout(new BoxLayout(listeningPortPanel, BoxLayout.X_AXIS));
        listeningPortPanel.add(listeningPortJLabel);
        listeningPortPanel.add(listeningPortJTextField);

        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        inputPanel.add(ipPanel);
        inputPanel.add(portPanel);
        inputPanel.add(usernamePanel);
        inputPanel.add(listeningPortPanel);
        inputPanel.add(submitButton);

        fileTreePane.getViewport().add(fileTree);
        fileTreePanel.add(fileTreePane);
        fileTreePanel.setPreferredSize(new Dimension(200, 450));
        fileTreePane.setPreferredSize(new Dimension(200, 450));

        selectedTreePane.getViewport().add(selectedFileTree);
        selectedTreePanel.add(selectedTreePane);
        selectedTreePanel.add(addFileButton);
        selectedTreePanel.add(removeFileButton);
        selectedTreePanel.setPreferredSize(new Dimension(200, 350));
        selectedTreePane.setPreferredSize(new Dimension(200, 350));
        selectedTreePanel.setLayout(new BoxLayout(selectedTreePanel,BoxLayout.Y_AXIS));
        usersListPanel.setPreferredSize(new Dimension(200, 350));
        usersFilesPanel.setPreferredSize(new Dimension(200, 350));
        downloadButton.addActionListener(this);

        usersFilesPanel.setLayout(new BoxLayout(usersFilesPanel, BoxLayout.Y_AXIS));

        usersListScrollPane.getViewport().add(usersList);

        usersList.addListSelectionListener(new ListSelectionListener() {

            private User findUserByAlias (String alias) {
                for (User user : usersListSource) {
                    if (user.getAlias().equals(alias)) {
                        return user;
                    }
                }
                return null;
            }

            @Override
            public void valueChanged(ListSelectionEvent e) {
                downloadedFilesTRee = new JTree(findUserByAlias(usersList.getSelectedValue()).getSharedTree());
                usersFilesScrollPane.getViewport().add(downloadedFilesTRee);
            }
        });

        DefaultListModel listModel = new DefaultListModel();

        for(User u : usersListSource){
            listModel.addElement(u.getAlias());
        }

        usersList.setModel(listModel);
        usersListPanel.add(usersListScrollPane);
        usersFilesPanel.add(usersFilesScrollPane);
        usersFilesPanel.add(downloadButton);
        usersFilesPanel.setLayout(new BoxLayout(usersFilesPanel,BoxLayout.Y_AXIS));


        managementPane.addTab("Sharing", UIManager.getIcon("FileChooser.upFolderIcon"), treesPanel,
                "Select the files and folders you want to share with other users");
        managementPane.addTab("Download", UIManager.getIcon("FileView.floppyDriveIcon"), usersPanel,
                "Search through what other users have shared");


        submitButton.addActionListener(this);
        addFileButton.addActionListener(this);
        removeFileButton.addActionListener(this);
        fileChooserButton.addActionListener(this);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        this.add(inputPanel);
        this.add(managementPane);
        this.setVisible(true);
    }

    private void connectToServer (int listeningPort) throws IOException, ClassNotFoundException {

        socketToServer = new Socket(ipAddress, port);
        objectOutputStreamToServer = null;
        objectInputStreamToServer = null;
        objectOutputStreamToServer = new ObjectOutputStream(socketToServer.getOutputStream());
        objectInputStreamToServer = new ObjectInputStream(socketToServer.getInputStream());

        ClientRequest request = new ClientRequest();
        request.setRequestCode(request.REGISTER);
        request.setUsername(username);
        request.setFileTreeToShare((DefaultMutableTreeNode) selectedFileTree.getModel().getRoot());
        request.setListeningPort(listeningPort);
        objectOutputStreamToServer.writeObject(request);

        ServerResponse serverResponse = (ServerResponse) objectInputStreamToServer.readObject();
        usersListSource = serverResponse.getUsersList();

        DefaultListModel<String> listModel = new DefaultListModel<String>();
        for(User u : usersListSource){
            listModel.addElement(u.getAlias());
        }
        usersList.setModel(listModel);

    }

    private void openListenSocket (int port) {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientListeningSocketThread clientListeningSocketThread = ClientListeningSocketThread.getInstance(serverSocket);
        clientListeningSocketThread.start();

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
                fileTreePane.getViewport().add(fileTree);
                this.revalidate();
                this.repaint();
            }
            changedDirectory = true;
        } else if (actionEvent.getSource() == addFileButton){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)fileTree.getLastSelectedPathComponent();
            DefaultMutableTreeNode sourceTree = (DefaultMutableTreeNode)fileTree.getModel().getRoot();
            DefaultMutableTreeNode originalTree = (DefaultMutableTreeNode)selectedFileTree.getModel().getRoot();
            selectedFileTree = new JTree((DefaultMutableTreeNode)addNode(node,originalTree));
            selectedTreePane.getViewport().add(selectedFileTree);
        } else if (actionEvent.getSource() == removeFileButton){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedFileTree.getLastSelectedPathComponent();
            ((DefaultTreeModel)selectedFileTree.getModel()).removeNodeFromParent(node);
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
            String listeningPortStr = listeningPortJTextField.getText();

            try {
                connectToServer(Integer.parseInt(listeningPortStr));
                openListenSocket(Integer.parseInt(listeningPortStr));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if (actionEvent.getSource() == downloadButton) {

            User selectedUser = findUserByAlias(usersList.getSelectedValue());
            if (selectedUser == null) {
                return;
            }
            Socket socket = new Socket();
            try {
                System.out.println(selectedUser);
                socket = new Socket(selectedUser.getIpAddress(), selectedUser.getListeningPort());
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
            TODO - Call download file with correct params
             */
//            this.downloadFile();
            String selectedNodePath = downloadedFilesTRee.getSelectionPath().toString();
            String fileName = downloadedFilesTRee.getName();
            selectedNodePath = selectedNodePath.substring(1, selectedNodePath.length() - 1);
            selectedNodePath = selectedNodePath.replace(", ", "\\");

            WriteFiles(socket, selectedNodePath, fileName);
        }
    }

    public void WriteFiles(Socket socket, String remotePath, String localPath){
        System.out.println("localPath: " + localPath);
        File newFile = new File(localPath);
        downloadFile(socket,remotePath, localPath);
        if(newFile.isDirectory()){
            File[] files = newFile.listFiles();
            for(File child : files){
                WriteFiles(socket, remotePath + "\\" + newFile.getName(), localPath + "\\" + newFile.getName());
            }
        }
    }

    public DefaultMutableTreeNode makeTree(DefaultMutableTreeNode root, File file)
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
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

    public DefaultMutableTreeNode addNode(DefaultMutableTreeNode addedNode, DefaultMutableTreeNode originalNode){

        if(changedDirectory == true){
            DefaultMutableTreeNode newFolder = (DefaultMutableTreeNode) addedNode.getParent();
            while(newFolder.getParent() != null){
                newFolder = (DefaultMutableTreeNode) addedNode.getPreviousLeaf();
            }
            newFolder.removeAllChildren();
            ((DefaultMutableTreeNode)newFolder.getRoot()).add(addedNode);
            originalNode.add(newFolder);
            changedDirectory = false;
        } else {
            ((DefaultMutableTreeNode)originalNode.getLastChild()).add(addedNode);
        }

        return originalNode;
    }

    private User findUserByAlias (String alias) {
        for (User user : usersListSource) {
            if (user.getAlias().equals(alias)) {
                return user;
            }
        }
        return null;
    }

    private void downloadFile (Socket socket, String localFilenameOfFileToBeSaved, String pathToRemoteFile) {

        System.out.println("local: " + localFilenameOfFileToBeSaved);
        System.out.println("remote " + pathToRemoteFile);

        ClientToClientRequestSenderThread senderThread = new ClientToClientRequestSenderThread();
        senderThread.setLocalPathToFile(SAVE_FOLDER + localFilenameOfFileToBeSaved);
        senderThread.setPathToFile(pathToRemoteFile);
        senderThread.setSocket(socket);
        senderThread.start();
    }

}
