package client.logic;

public class ClientToClientRequest {

    public static final int TRANSFER = 100;

    String filename;
    int code;

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
