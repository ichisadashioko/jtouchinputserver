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
import java.util.Iterator;
import java.util.Random;

class Point2D {
    public int x;
    public int y;
}

class CustomCanvasState {
    public int componentWidth;
    public int componentHeight;
    public int renderWidth;
    public int renderHeight;
    public float scale;
    public int renderOffsetX;
    public int renderOffsetY;
    public float scaleTmpX;
    public float scaleTmpY;
    public ArrayList<ArrayList<Point2D>> scaledStrokes;
    public ArrayList<Color> strokeColors;

    public CustomCanvasState() {
        this.componentWidth = 0;
        this.componentHeight = 0;
        this.renderWidth = 0;
        this.renderHeight = 0;
        this.renderOffsetX = 0;
        this.renderOffsetY = 0;
        this.scale = 0f;
        this.scaleTmpX = 0f;
        this.scaleTmpY = 0f;
        this.scaledStrokes = new ArrayList<>();
        this.strokeColors = new ArrayList<>();
    }
}

class CustomCanvas extends Canvas {
    public int widthAspectRatio;
    public int heightAspectRatio;
    public ArrayList<ArrayList<Point2D>> strokes;
    public CustomCanvasState componentState;
    public Random rand;

    public CustomCanvas() {
        super();
        this.componentState = new CustomCanvasState();
        this.rand = new Random();
        this.widthAspectRatio = 1;
        this.heightAspectRatio = 1;
    }

    public Color generateColor() {
        int red = rand.nextInt(256);
        int green = rand.nextInt(256);
        int blue = rand.nextInt(256);
        Color color = new Color(red, green, blue);
        return color;
    }

    public void addNewPoint(Point2D point2d) {
        ArrayList<Point2D> currentStroke = null;
        int numStrokes = this.strokes.size();
        if (numStrokes == 0) {
            currentStroke = new ArrayList<>();
            this.strokes.add(currentStroke);
        } else {
            currentStroke = this.strokes.get(numStrokes - 1);
        }

        ArrayList<Point2D> currentScaledStroke = null;
        int numScaledStrokes = this.componentState.scaledStrokes.size();
        if (numScaledStrokes == 0) {
            currentScaledStroke = new ArrayList<>();
            this.componentState.scaledStrokes.add(currentScaledStroke);
        } else {
            currentScaledStroke = this.componentState.scaledStrokes.get(numScaledStrokes - 1);
        }

        if (this.componentState.strokeColors.size() == 0) {
            this.componentState.strokeColors.add(this.generateColor());
        }

        Point2D scaledPoint = new Point2D();
        scaledPoint.x = (int) (this.componentState.scaleTmpX * point2d.x);
        scaledPoint.y = (int) (this.componentState.scaleTmpY * point2d.y);

        currentScaledStroke.add(scaledPoint);
    }

    public void addNewStroke(Point2D point2d) {
        ArrayList<Point2D> newStroke = new ArrayList<>();
        newStroke.add(point2d);
        this.strokes.add(newStroke);

        ArrayList<Point2D> newScaledStroke = new ArrayList<>();
        Point2D scaledPoint = new Point2D();
        scaledPoint.x = (int) (this.componentState.scaleTmpX * point2d.x);
        scaledPoint.y = (int) (this.componentState.scaleTmpY * point2d.y);
        newScaledStroke.add(scaledPoint);
        this.componentState.scaledStrokes.add(newScaledStroke);

        this.componentState.strokeColors.add(this.generateColor());
    }

    public void setAspectRatio(int widthAspectRatio, int heightAspectRatio) {
        this.widthAspectRatio = widthAspectRatio;
        this.heightAspectRatio = heightAspectRatio;
        this.refreshState();
    }

    public void setStrokes(ArrayList<ArrayList<Point2D>> strokes) {
        this.strokes = strokes;
        this.refreshState();
    }

    public void refreshState() {
        int componentWidth = this.getWidth();
        int componentHeight = this.getHeight();

        this.componentState.componentWidth = componentWidth;
        this.componentState.componentHeight = componentHeight;

        float widthScale = ((float) componentWidth) / ((float) this.widthAspectRatio);
        float heightScale = ((float) componentHeight) / ((float) this.heightAspectRatio);

        this.componentState.scale = Math.min(widthScale, heightScale);

        this.componentState.renderWidth = (int) (componentWidth * this.componentState.scale);
        this.componentState.renderHeight = (int) (componentHeight * this.componentState.scale);

        this.componentState.renderOffsetX = (componentWidth - this.componentState.renderWidth) / 2;
        this.componentState.renderOffsetY = (componentHeight - this.componentState.renderWidth) / 2;

        this.componentState.scaleTmpX = this.componentState.renderWidth / 256f;
        this.componentState.scaleTmpY = this.componentState.renderHeight / 256f;

        this.componentState.scaledStrokes = new ArrayList<>();
        this.componentState.strokeColors = new ArrayList<>();

        Iterator<ArrayList<Point2D>> strokeIterator = this.strokes.iterator();

        while (strokeIterator.hasNext()) {
            Color color = this.generateColor();
            this.componentState.strokeColors.add(color);

            ArrayList<Point2D> stroke = strokeIterator.next();
            Iterator<Point2D> pointIterator = stroke.iterator();
            ArrayList<Point2D> scaledStroke = new ArrayList<>();

            while (pointIterator.hasNext()) {
                Point2D point2d = pointIterator.next();
                Point2D scaledPoint = new Point2D();
                scaledPoint.x =
                        ((int) (point2d.x * this.componentState.scaleTmpX))
                                + this.componentState.renderOffsetX;
                scaledPoint.y =
                        ((int) (point2d.y * this.componentState.scaleTmpY))
                                + this.componentState.renderOffsetY;

                scaledStroke.add(scaledPoint);
            }

            this.componentState.scaledStrokes.add(scaledStroke);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int componentWidth = this.getWidth();
        int componentHeight = this.getHeight();

        if ((componentWidth != this.componentState.componentWidth)
                || (componentHeight != this.componentState.componentHeight)) {
            this.refreshState();
        }

        Iterator<ArrayList<Point2D>> strokeIter = this.componentState.scaledStrokes.iterator();
        Iterator<Color> colorIter = this.componentState.strokeColors.iterator();

        while (strokeIter.hasNext() && colorIter.hasNext()) {
            Color strokeColor = colorIter.next();
            ArrayList<Point2D> stroke = strokeIter.next();
            if (stroke.size() < 2) {
                continue;
            }

            g.setColor(strokeColor);

            Iterator<Point2D> pointIter = stroke.iterator();
            Point2D prevPoint = pointIter.next();

            while (pointIter.hasNext()) {
                Point2D curPoint = pointIter.next();
                g.drawLine(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y);
                prevPoint = curPoint;
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

        int widthAspectRatio = inputStream.read();
        int heightAspectRatio = inputStream.read();

        System.out.println("widthAspectRatio: " + Integer.toString(widthAspectRatio));
        System.out.println("heightAspectRatio: " + Integer.toString(heightAspectRatio));

        outputStream.write(ServerCommands.START);

        Frame frame = new Frame("jtouchintpuserver");

        CustomCanvas canvas = new CustomCanvas();
        canvas.setAspectRatio(widthAspectRatio, heightAspectRatio);
        canvas.setStrokes(strokes);

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
