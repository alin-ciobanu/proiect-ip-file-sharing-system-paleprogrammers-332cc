package client.logic;


public class ClientToClientResponse {

    public static final int FILE_RESPONSE = 1001;

    private int code;
    byte[] file;
    long fileLength;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }
}
