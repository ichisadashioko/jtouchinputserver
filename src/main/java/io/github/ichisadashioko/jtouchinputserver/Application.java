package io.github.ichisadashioko.jtouchinputserver;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Application extends Frame {
    public static final String DEFAULT_TITLE = "JTouch Input Server";
    public static final int DEFAULT_WIDTH = 640;
    public static final int DEFAULT_HEIGHT = 480;
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
    public static final int DEFAULT_PORT = 9000;

    public TextField portNumberTextField;
    public boolean isServerRunning;
    public Button startServerButton;

    public Application() {
        super(DEFAULT_TITLE);
        this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.setBackground(DEFAULT_BACKGROUND_COLOR);

        this.isServerRunning = false;
        this.portNumberTextField = new TextField(Integer.toString(DEFAULT_PORT));
        this.startServerButton = new Button("start server");

        this.startServerButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println(e);
                    }
                });

        GridLayout gridLayout = new GridLayout(2, 2);
        this.setLayout(gridLayout);

        this.add(portNumberTextField);
        this.add(startServerButton);
    }
}
