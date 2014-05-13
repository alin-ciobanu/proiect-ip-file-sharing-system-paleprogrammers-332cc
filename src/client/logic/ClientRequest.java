package client.logic;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Serializable;

public class ClientRequest implements Serializable {

    public static final int CLOSE = 102;
    public static final int GET = 302;
    public static final int REGISTER = 103;

    private int requestCode;
    private int listeningPort;

    private String username;
    private DefaultMutableTreeNode fileTreeToShare;

    public int getListeningPort() {
        return listeningPort;
    }

    public void setListeningPort(int listeningPort) {
        this.listeningPort = listeningPort;
    }

    public DefaultMutableTreeNode getFileTreeToShare() {
        return fileTreeToShare;
    }

    public void setFileTreeToShare(DefaultMutableTreeNode fileTreeToShare) {
        this.fileTreeToShare = fileTreeToShare;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

}
