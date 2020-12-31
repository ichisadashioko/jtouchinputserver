package io.github.ichisadashioko.jtouchinputserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(9090);

        Socket clientSocket = serverSocket.accept();

        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();

        int width_aspect_ratio = inputStream.read();
        int height_aspect_ratio = inputStream.read();

        System.out.println("width_aspect_ratio: " + Integer.toString(width_aspect_ratio));
        System.out.println("height_aspect_ratio: " + Integer.toString(height_aspect_ratio));

        while (true) {
            int intBuffer = inputStream.read();
            System.out.println(intBuffer);
            if (intBuffer < 0) {
                break;
            }
        }

        serverSocket.close();
    }
}
