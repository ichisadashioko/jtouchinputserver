package io.github.ichisadashioko.jtouchinputserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TouchInputClientHandler extends Thread {

    public static final int DEFAULT_INITIALIZING_COMMUNICATION_TIMEOUT = 5000;

    public Socket clientSocket;

    public boolean isInitialized;
    public int clientWidth;
    public int clientHeight;
    public boolean isDisconnected;

    public InputStream inputStream;
    public OutputStream outputStream;
    public InetAddress clientAddress;

    public TouchInputClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.isInitialized = false;
        this.clientWidth = -1;
        this.clientHeight = -1;
        this.inputStream = null;
        this.outputStream = null;
        this.clientAddress = null;
        this.isDisconnected = false;
    }

    public void initializeClient() throws Exception {
        this.clientAddress = this.clientSocket.getInetAddress();
        this.inputStream = this.clientSocket.getInputStream();
        this.outputStream = this.clientSocket.getOutputStream();

        // ask the client about their device's dimension
        this.clientSocket.setSoTimeout(DEFAULT_INITIALIZING_COMMUNICATION_TIMEOUT);
        // width first then height

        int intBuffer = -1;

        try {
            intBuffer = this.inputStream.read();
        } catch (SocketTimeoutException ex) {
            // DEBUG
            ex.printStackTrace(System.err);
            throw new Exception(
                    "Client was too slow to send data about the device aspect ratio width!");
        }

        if (intBuffer < 1) {
            throw new Exception(
                    "Received invalid aspect ratio with! It must be greater than one! Received value: "
                            + intBuffer);
        }

        this.clientWidth = intBuffer;

        try {
            intBuffer = this.inputStream.read();
        } catch (SocketTimeoutException ex) {
            // DEBUG
            ex.printStackTrace(System.err);
            throw new Exception(
                    "Client was too slow to send data about the number of bytes to store the device height aspect ratio!");
        }

        if (intBuffer < 1) {
            throw new Exception(
                    "Client did not send any data about the number of bytes to store the device height aspect ratio!");
        }

        this.clientHeight = intBuffer;
        // TODO continue here
    }

    public void run() {
        try {
            initializeClient();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
