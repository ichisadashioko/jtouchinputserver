package io.github.ichisadashioko.jtouchinputserver;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ServerSocket;

public class Application extends Frame {
    public static final String DEFAULT_TITLE = "JTouch Input Server";
    public static final int DEFAULT_WIDTH = 640;
    public static final int DEFAULT_HEIGHT = 480;
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
    public static final int DEFAULT_PORT = 0;

    public TextField portNumberTextField;
    public boolean isServerRunning;
    public Button startServerButton;
    public Canvas touchInputCanvas;
    public Choice clientCombobox;

    public TouchInputServer server;

    public Application() {
        super(DEFAULT_TITLE);
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        // this.setResizable(false);
        // this.setBackground(DEFAULT_BACKGROUND_COLOR);
        this.setLocationRelativeTo(null);
        this.setLayout(null);

        this.server = null;

        this.isServerRunning = false;
        this.portNumberTextField = new TextField(Integer.toString(DEFAULT_PORT));
        this.startServerButton = new Button("start server");
        this.touchInputCanvas = new Canvas();
        this.clientCombobox = new Choice();

        this.startServerButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println(e);

                        if (server != null) {
                            System.out.println("The server is running!");
                        } else {
                            int port = Validator.validatePortString(portNumberTextField.getText());
                            if (port == -1) {
                                System.out.println(
                                        "Invalid port number! Server has not been started!");
                            } else {
                                ServerSocket serverSocket = null;
                                try {
                                    serverSocket = new ServerSocket(port);
                                } catch (Exception ex) {
                                    ex.printStackTrace(System.err);
                                    System.err.println("Failed to create the ServerSocket object!");
                                }

                                if (serverSocket != null) {
                                    server = new TouchInputServer();
                                    server.serverSocket = serverSocket;
                                    int boundPort = serverSocket.getLocalPort();
                                    server.initialBoundPort = boundPort;
                                    if (boundPort != port) {
                                        portNumberTextField.setText(Integer.toString(boundPort));
                                    }
                                    server.start();
                                }
                            }
                        }
                    }
                });

        // this.setLayout(new GridBagLayout());

        // GridBagConstraints gridBagConstraints;

        // gridBagConstraints = new GridBagConstraints();
        // gridBagConstraints.gridx = 0;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.weightx = 6;
        // gridBagConstraints.fill = GridBagConstraints.BOTH;
        // this.add(this.clientCombobox, gridBagConstraints);

        // this.clientCombobox.setPreferredSize(new Dimension(480, 30));

        // gridBagConstraints = new GridBagConstraints();
        // gridBagConstraints.gridx = 1;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.weightx = 1;
        // gridBagConstraints.fill = GridBagConstraints.BOTH;
        // this.add(this.portNumberTextField, gridBagConstraints);
        // this.portNumberTextField.setPreferredSize(new Dimension(80, 30));

        // this.portNumberTextField.setPreferredSize(new Dimension(80, 30));

        // gridBagConstraints = new GridBagConstraints();
        // gridBagConstraints.gridx = 2;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.weightx = 1;
        // gridBagConstraints.fill = GridBagConstraints.BOTH;
        // this.add(startServerButton, gridBagConstraints);

        // gridBagConstraints = new GridBagConstraints();
        // gridBagConstraints.gridx = 0;
        // gridBagConstraints.gridy = 1;
        // gridBagConstraints.weightx = 8;
        // gridBagConstraints.weighty = 3;
        // gridBagConstraints.fill = GridBagConstraints.BOTH;
        // this.add(touchInputCanvas, gridBagConstraints);

        this.add(touchInputCanvas);
        this.add(this.clientCombobox);
        this.add(this.portNumberTextField);
        this.add(this.startServerButton);

        this.clientCombobox.setBounds(0, 0, 480, 30);
        this.portNumberTextField.setBounds(480, 0, 80, 30);
        this.startServerButton.setBounds(560, 0, 80, 30);
        this.touchInputCanvas.setBounds(0, 30, 640, 450);
    }
}
