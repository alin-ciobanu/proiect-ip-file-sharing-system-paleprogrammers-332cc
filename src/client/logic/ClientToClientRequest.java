package client.logic;

import java.io.Serializable;

public class ClientToClientRequest implements Serializable {

    public static final int TRANSFER = 100;

    String filename;
    int code;

    @Override
    public String toString() {
        return "ClientToClientRequest{" +
                "filename='" + filename + '\'' +
                ", code=" + code +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
