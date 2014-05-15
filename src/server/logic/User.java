package server.logic;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created by Alin on 08/05/14.
 */
public class User implements Serializable {
    private String alias;
    private DefaultMutableTreeNode sharedTree;
    private transient Socket socket;
    private String ipAddress;
    private int listeningPort;

    @Override
    public String toString() {
        return "User{" +
                "alias='" + alias + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", listeningPort=" + listeningPort +
                '}';
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public void setListeningPort(int listeningPort) {
        this.listeningPort = listeningPort;
    }

    public DefaultMutableTreeNode getSharedTree() {
        return sharedTree;
    }

    public void setSharedTree(DefaultMutableTreeNode sharedTree) {
        this.sharedTree = sharedTree;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getAlias(){
        return alias;
    }

    public void setAlias(String alias){
        this.alias = alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (alias != null ? !alias.equals(user.alias) : user.alias != null) return false;
        if (sharedTree != null ? !sharedTree.equals(user.sharedTree) : user.sharedTree != null) return false;
        if (socket != null ? !socket.equals(user.socket) : user.socket != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = alias != null ? alias.hashCode() : 0;
        result = 31 * result + (sharedTree != null ? sharedTree.hashCode() : 0);
        result = 31 * result + (socket != null ? socket.hashCode() : 0);
        return result;
    }
}
