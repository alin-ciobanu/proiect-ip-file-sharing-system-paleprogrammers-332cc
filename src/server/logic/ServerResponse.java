package server.logic;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class ServerResponse implements Serializable {

    byte[] fileAsBytes = null;
    long fileLength;

    ArrayList<User> usersList;

    public ArrayList<User> getUsersList() {
        return usersList;
    }

    public void setUsersList(ArrayList<User> usersList) {
        this.usersList = usersList;
    }

    public byte[] getFileAsBytes() {
        return fileAsBytes;
    }

    public long getFileLength () {
        return fileLength;
    }

    public void readFile(String fileString) {

        File file = new File(fileString);
        this.fileLength = file.length();
        try {
            fileAsBytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
