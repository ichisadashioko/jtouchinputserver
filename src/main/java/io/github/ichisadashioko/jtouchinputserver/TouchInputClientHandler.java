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

    public void initialize() throws Exception {
        this.clientAddress = this.clientSocket.getInetAddress();
        this.inputStream = this.clientSocket.getInputStream();
        this.outputStream = this.clientSocket.getOutputStream();

        // ask the client about their device's dimension
        this.clientSocket.setSoTimeout(DEFAULT_INITIALIZING_COMMUNICATION_TIMEOUT);

        // TODO read two bytes at once
        // byte[] dimensionBuffer = new byte[2];
        // this.inputStream.read(dimensionBuffer);

        int intBuffer = -1;

        try {
            intBuffer = this.inputStream.read();
        } catch (SocketTimeoutException ex) {
            // DEBUG
            ex.printStackTrace(System.err);
            throw new Exception("Client was too slow to send the device aspect ratio width value!");
        }

        if (intBuffer < 1) {
            throw new Exception(
                    "Received invalid aspect ratio width value! It must be greater than 1! Received value: "
                            + intBuffer);
        }

        if (intBuffer > 255) {
            throw new Exception(
                    "Received invalid aspect ratio width value! It must be less than 255! Received value: "
                            + intBuffer);
        }

        this.clientWidth = intBuffer;

        try {
            intBuffer = this.inputStream.read();
        } catch (SocketTimeoutException ex) {
            // DEBUG
            ex.printStackTrace(System.err);
            throw new Exception(
                    "Client was too slow to send data the device aspect ratio height value!");
        }

        if (intBuffer < 1) {
            throw new Exception(
                    "Received invalid aspect ratio height value! It must be greater than 1! Received value: "
                            + intBuffer);
        }

        if (intBuffer > 255) {
            throw new Exception(
                    "Received invalid aspect ratio height value! It must be less than 255! Received value: "
                            + intBuffer);
        }

        this.clientHeight = intBuffer;
        this.isInitialized = true;
    }

    public void run() {
        try {
            initialize();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
