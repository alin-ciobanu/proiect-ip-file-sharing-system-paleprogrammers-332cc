package client.ui;

import client.logic.ClientRequest;
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
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientPanel extends JPanel implements ActionListener {

    Socket socketToServer;
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

    JPanel usersListPanel = new JPanel();
    JPanel usersFilesPanel = new JPanel();

    JList usersList = new JList();

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

        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        inputPanel.add(ipPanel);
        inputPanel.add(portPanel);
        inputPanel.add(usernamePanel);
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

        usersListScrollPane.getViewport().add(usersList);

        User user1 = new User();
        user1.alias = "Test1";
        user1.sharedTree = new DefaultMutableTreeNode("Test1");
        User user2 = new User();
        user2.alias = "Test2";
        user2.sharedTree = new DefaultMutableTreeNode("Test2");
        usersListSource.add(user1);
        usersListSource.add(user2);

        usersList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                    DefaultMutableTreeNode root = usersListSource.get(usersList.getSelectedIndex()).getSharedTree();
                    downloadedFilesTRee = new JTree(root);
                    usersFilesScrollPane.getViewport().add(new JTree(root));
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

    private void connectToServer () throws IOException, ClassNotFoundException {

        socketToServer = new Socket(ipAddress, port);
        objectOutputStreamToServer = null;
        objectInputStreamToServer = null;
        objectOutputStreamToServer = new ObjectOutputStream(socketToServer.getOutputStream());
        objectInputStreamToServer = new ObjectInputStream(socketToServer.getInputStream());

        ClientRequest request = new ClientRequest();
        request.setRequestCode(request.REGISTER);
        request.setUsername(username);
        request.setFileTreeToShare((DefaultMutableTreeNode) selectedFileTree.getModel().getRoot());
        objectOutputStreamToServer.writeObject(request);

        ServerResponse serverResponse = (ServerResponse) objectInputStreamToServer.readObject();
        usersListSource = serverResponse.getUsersList();

        DefaultListModel listModel = new DefaultListModel();
        for(User u : usersListSource){
            listModel.addElement(u.getAlias());
        }
        usersList.setModel(listModel);

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

            try {
                connectToServer();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
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

}
