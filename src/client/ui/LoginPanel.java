package client.ui;

import javax.swing.*;

public class LoginPanel extends JPanel {

    JTextField usernameTextField;
    JLabel usernameLabel;
    JPanel usernamePanel;
    JPasswordField passwordField;
    JLabel passwordLabel;
    JPanel passwordPanel;
    JButton submitLoginButton;

    public LoginPanel () {

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        usernameTextField = new JTextField();
        passwordField = new JPasswordField();
        submitLoginButton = new JButton("Submit");
        usernameLabel = new JLabel("Username");
        passwordLabel = new JLabel("Password");
        usernamePanel = new JPanel();
        passwordPanel = new JPanel();

        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
        usernamePanel.setVisible(true);
        passwordPanel.setVisible(true);

        this.add(usernamePanel);
        this.add(passwordPanel);
        this.add(submitLoginButton);

        this.setVisible(true);

    }

}
