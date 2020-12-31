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

        outputStream.write(ServerCommands.START);

        while (true) {
            int intBuffer = inputStream.read();
            System.out.println(intBuffer);
            if (intBuffer < 0) {
                break;
            }

            byte touch_event_type = (byte) (intBuffer & 0b11);
            int x_axis_data = (intBuffer & 0b100) >> 2;
            int y_axis_data = (intBuffer & 0b1000) >> 3;
            int touch_id = intBuffer >> 4;

            if (x_axis_data > 0) {
                x_axis_data = inputStream.read() + 1;
            }

            if (y_axis_data > 0) {
                y_axis_data = inputStream.read() + 1;
            }

            System.out.println("touch_event_type: " + Integer.toString(touch_event_type));
            System.out.println("touch_id: " + Integer.toString(touch_id));
            System.out.println("x_axis_data: " + Integer.toString(x_axis_data));
            System.out.println("y_axis_data: " + Integer.toString(y_axis_data));
        }

        serverSocket.close();
    }
}
