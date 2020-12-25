package io.github.ichisadashioko.jtouchinputserver;

import java.io.*;
import java.net.*;
import java.util.*;

public class TouchInputServer extends Thread {
    public static final int DEFAULT_SO_TIMEOUT = 100;
    public static final long DEFAULT_PAUSE_AND_CHECK_TO_CONTINUE_TIMEOUT = 1000L;

    public int port;
    public ServerSocket serverSocket;
    public List<TouchInputClientHandler> clientList;
    public boolean isStop;
    public boolean isContinueAcceptingClient;

    public TouchInputServer(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
        this.clientList = new ArrayList<>();
        this.isStop = false;
        this.isContinueAcceptingClient = true;

        this.serverSocket.setSoTimeout(DEFAULT_SO_TIMEOUT);
    }

    public void startServer() throws IOException, InterruptedException {
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (SocketTimeoutException e) {
                continue;
            }

            TouchInputClientHandler clientHandler = new TouchInputClientHandler(clientSocket);

            synchronized (this.clientList) {
                this.clientList.add(clientHandler);
            }

            if (this.isStop) {
                this.serverSocket.close();
                break;
            }

            if (!this.isContinueAcceptingClient) {
                // pause
                while (true) {
                    Thread.sleep(DEFAULT_PAUSE_AND_CHECK_TO_CONTINUE_TIMEOUT);
                    synchronized (this) {
                        if (this.isContinueAcceptingClient) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public void run() {
        try {
            this.startServer();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
