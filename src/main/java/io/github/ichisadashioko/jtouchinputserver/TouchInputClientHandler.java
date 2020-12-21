package io.github.ichisadashioko.jtouchinputserver;

import java.net.Socket;

public class TouchInputClientHandler extends Thread {
    public Socket clientSocket;

    public TouchInputClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run(){
        try {

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
