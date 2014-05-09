package client.ui;

import client.logic.ClientRequest;
import org.apache.commons.io.FileUtils;
import server.logic.ServerResponse;

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

    JTree fileTree = new JTree(new DefaultMutableTreeNode());
    JTree selectedFileTree = new JTree(new DefaultMutableTreeNode());

    JLabel aliasLabel = new JLabel("ALIAS");
    JLabel ipLabel = new JLabel("IP");
    JLabel portLabel = new JLabel("PORT");
    JLabel fileNameLabel = new JLabel("Filename");

    JTextField aliasTextField = new JTextField();
    JTextField ipTextField = new JTextField();
    JTextField portTextField = new JTextField();
    JTextField fileNameTextField = new JTextField();

    JPanel inputPanel = new JPanel();
    JPanel aliasPanel = new JPanel();
    JPanel ipPanel = new JPanel();
    JPanel portPanel = new JPanel();
    JPanel fileNamePanel = new JPanel();
    JPanel fileTreePanel = new JPanel();
    JPanel selectedTreePanel = new JPanel();
    JSplitPane treesPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,fileTreePanel,selectedTreePanel);

    JScrollPane fileTreePane = new JScrollPane();
    JScrollPane selectedTreePane = new JScrollPane();

    JButton submitButton = new JButton("Get");
    JButton fileChooserButton = new JButton("Choose File");
    JFileChooser fileChooser = new JFileChooser();

    String ipAddress;
    int port;
    String file;

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

        fileNamePanel.setLayout(new BoxLayout(fileNamePanel, BoxLayout.X_AXIS));
        fileNamePanel.add(fileNameLabel);
        fileNamePanel.add(fileNameTextField);
        fileNamePanel.add(fileChooserButton);
        fileNameTextField.setPreferredSize(new Dimension(100,10));

        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        inputPanel.add(ipPanel);
        inputPanel.add(portPanel);
        inputPanel.add(fileNamePanel);
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
