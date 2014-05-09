package server.logic;

import javax.swing.*;

/**
 * Created by Alin on 08/05/14.
 */
public class User {
    private String alias;
    private int ip;
    private JTree fileSystem;

    public User(String alias, int ip, JTree fileSystem){
        this.alias = alias;
        this.ip = ip;
        this.fileSystem = fileSystem;
    }

    public User(){
        alias = "";
        fileSystem = new JTree();
    }

    public String getAlias(){
        return alias;
    }

    public int getIp(){
        return ip;
    }

    public JTree getFileSystem(){
        return fileSystem;
    }

    public void setAlias(String alias){
        this.alias = alias;
    }
    public void setIp(int ip){
        this.ip = ip;
    }
    public void setFileSystem(JTree fileSystem){
        this.fileSystem = fileSystem;
    }
}
