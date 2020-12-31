package io.github.ichisadashioko.jtouchinputserver;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

class Point2D {
    public int x;
    public int y;
}

class CustomCanvas extends Canvas {
    public int width_aspect_ratio;
    public int height_aspect_ratio;
    // public float aspectRatio;
    public ArrayList<ArrayList<Point2D>> strokes;

    @Override
    public void paint(Graphics g) {
        // TODO Auto-generated method stub
        super.paint(g);

        // TODO cache scaling operations
        int componentWidth = this.getWidth();
        int componentHeight = this.getHeight();

        float widthScale = ((float) componentWidth) / ((float) this.width_aspect_ratio);
        float heightScale = ((float) componentHeight) / ((float) this.height_aspect_ratio);

        float scale = Math.min(widthScale, heightScale);

        int scaledWidth = (int) (componentWidth * scale);
        int scaledHeight = (int) (componentHeight * scale);

        int offsetX = (componentWidth - scaledWidth) / 2;
        int offsetY = (componentHeight - scaledHeight) / 2;

        float scaleXTmp = scaledWidth / 256.0f;
        float scaleYTmp = scaledHeight / 256.0f;

        Random rand = new Random();

        int numStrokes = this.strokes.size();

        for (int i = 0; i < numStrokes; i++) {
            int red = rand.nextInt(256);
            int green = rand.nextInt(256);
            int blue = rand.nextInt(256);
            Color color = new Color(red, green, blue);
            g.setColor(color);

            ArrayList<Point2D> stroke = strokes.get(i);
            int numTouches = stroke.size();

            if (numTouches < 2) {
                // skip a single point stroke
                continue;
            }

            Point2D previousPoint = stroke.get(0);

            for (int j = 1; j < numTouches; j++) {
                Point2D currentPoint = stroke.get(j);

                int scaledPreviousX = ((int) (previousPoint.x * scaleXTmp)) + offsetX;
                int scaledPreviousY = (int) (previousPoint.y * scaleYTmp) + offsetY;

                int scaledCurrentX = (int) (currentPoint.x * scaleXTmp) + offsetX;
                int scaledCurrentY = (int) (currentPoint.y * scaleYTmp) + offsetY;
                g.drawLine(scaledPreviousX, scaledPreviousY, scaledCurrentX, scaledCurrentY);

                previousPoint = currentPoint;
            }
        }
    }
}

public class Main {
    public static void main(String[] args) throws Exception {

        ArrayList<ArrayList<Point2D>> strokes = new ArrayList<>();

        ServerSocket serverSocket = new ServerSocket(9090);

        Socket clientSocket = serverSocket.accept();

        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();

        int width_aspect_ratio = inputStream.read();
        int height_aspect_ratio = inputStream.read();

        System.out.println("width_aspect_ratio: " + Integer.toString(width_aspect_ratio));
        System.out.println("height_aspect_ratio: " + Integer.toString(height_aspect_ratio));

        outputStream.write(ServerCommands.START);

        Frame frame = new Frame("jtouchintpuserver");

        CustomCanvas canvas = new CustomCanvas();
        canvas.width_aspect_ratio = width_aspect_ratio;
        canvas.height_aspect_ratio = height_aspect_ratio;
        canvas.strokes = strokes;

        canvas.setSize(640, 480);

        frame.add(canvas);

        frame.setSize(640, 480);
        frame.setVisible(true);

        ArrayList<Point2D> currentStroke = new ArrayList<>();
        strokes.add(currentStroke);

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

            Point2D touchPos = new Point2D();
            touchPos.x = x_axis_data;
            touchPos.y = y_axis_data;

            if (touch_event_type == TouchEvents.TOUCH_DOWN) {
                currentStroke.add(touchPos);
            } else if (touch_event_type == TouchEvents.TOUCH_MOVE) {
                currentStroke.add(touchPos);
            } else if (touch_event_type == TouchEvents.TOUCH_UP) {
                currentStroke = new ArrayList<>();
                strokes.add(currentStroke);
            }
        }

        serverSocket.close();
    }
}
