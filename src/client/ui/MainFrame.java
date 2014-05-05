package client.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    LoginPanel loginPanel;


    public MainFrame () {

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());

        loginPanel = new LoginPanel();

        this.add(loginPanel);

        this.pack();
        this.setVisible(true);

    }

    public static void main (String[] args) {

        new MainFrame();

    }

}
