package server.logic;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class ServerResponse implements Serializable {

    byte[] fileAsBytes = null;
    long fileLength;

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
