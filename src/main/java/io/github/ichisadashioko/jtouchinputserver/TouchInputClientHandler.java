package io.github.ichisadashioko.jtouchinputserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TouchInputClientHandler extends Thread {

    public static final int DEFAULT_INITIALIZING_COMMUNICATION_TIMEOUT = 5000;

    public Socket socket;

    /**
     * If this object is not initialized, then all the other members expect for `socket` are invalid
     * and should not be used.
     */
    public boolean isInitialized;

    public int clientWidth;
    public int clientHeight;

    /**
     * When handling this object, if IOException is thrown, that would mean the client has
     * disconnected. You have the responsibility to set this member to `true`.
     */
    public boolean isDisconnected;

    public InputStream inputStream;
    public OutputStream outputStream;
    public InetAddress clientAddress;

    /**
     * You should be the one responsible to attach an appropriate event listener before "running"
     * this object.
     */
    public TouchInputClientEventListener listener;

    public TouchInputClientHandler(Socket socket) {
        super();

        this.socket = socket;
        this.isInitialized = false;
        this.clientWidth = -1;
        this.clientHeight = -1;
        this.inputStream = null;
        this.outputStream = null;
        this.clientAddress = null;
        this.isDisconnected = false;
        this.listener = null;
    }

    public void initialize() throws Exception {
        this.clientAddress = this.socket.getInetAddress();
        this.inputStream = this.socket.getInputStream();
        this.outputStream = this.socket.getOutputStream();

        // ask the client about their device's dimension
        // this.socket.setSoTimeout(DEFAULT_INITIALIZING_COMMUNICATION_TIMEOUT);

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

    public void cleanResources() {
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            } catch (IOException ex) {
                System.err.println("Failed to close inputStream!");
                ex.printStackTrace(System.err);
            }

            this.inputStream = null;
        }

        if (this.outputStream != null) {
            try {
                this.outputStream.close();
            } catch (IOException ex) {
                System.err.println("Failed to close outputStream!");
                ex.printStackTrace(System.err);
            }

            this.outputStream = null;
        }

        if (this.socket != null) {
            if (!this.socket.isClosed()) {
                try {
                    this.socket.close();
                } catch (IOException ex) {
                    System.err.println("Failed to close the socket!");
                    ex.printStackTrace(System.err);
                }
            }
        }

        this.isDisconnected = true;
    }

    public void run() {
        try {
            this.initialize();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
