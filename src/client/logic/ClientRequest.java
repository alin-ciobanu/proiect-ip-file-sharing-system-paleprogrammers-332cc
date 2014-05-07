package client.logic;

import java.io.Serializable;

public class ClientRequest implements Serializable {

    public final int GET = 101;
    public final int CLOSE = 102;

    private int requestCode;

    /*
        urmatoarele campuri vor fi setate doar daca requestCode va fi setat pe GET
     */
    private String whatFile; // what file you want to transfer


    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public String getWhatFile() {
        return whatFile;
    }

    public void setWhatFile(String whatFile) {
        this.whatFile = whatFile;
    }


}
