package client.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame () {

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridLayout());
        this.setPreferredSize(new Dimension(400, 450));

        ClientPanel clientPanel = new ClientPanel();
        this.add(clientPanel);

        this.pack();
        this.setVisible(true);

    }

    public static void main (String[] args) {

        new MainFrame();

    }

}
