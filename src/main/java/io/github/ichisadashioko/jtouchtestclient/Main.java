package io.github.ichisadashioko.jtouchtestclient;

import io.github.ichisadashioko.jtouchinputserver.ServerCommands;
import io.github.ichisadashioko.jtouchinputserver.TouchEvents;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Main {

    public static ArrayList<TouchData> generateStroke() {
        int deviceWidth = 1920;
        int deviceHeight = 1080;
        ArrayList<TouchData> retval = new ArrayList<>();

        TouchData touchDown = new TouchData();
        touchDown.deviceWidth = deviceWidth;
        touchDown.deviceHeight = deviceHeight;
        touchDown.touchEventType = TouchEvents.TOUCH_DOWN;
        touchDown.touchId = 0;
        touchDown.touchX = 0;
        touchDown.touchY = 0;
        retval.add(touchDown);

        TouchData touchMove = new TouchData();
        touchMove.deviceWidth = deviceWidth;
        touchMove.deviceHeight = deviceHeight;
        touchMove.touchEventType = TouchEvents.TOUCH_MOVE;
        touchMove.touchId = 0;
        touchMove.touchX = 1920;
        touchMove.touchY = 1080;

        retval.add(touchMove);

        TouchData touchUp = new TouchData();
        touchUp.deviceWidth = deviceWidth;
        touchUp.deviceHeight = deviceHeight;
        touchUp.touchEventType = TouchEvents.TOUCH_UP;
        touchUp.touchId = 0;
        touchUp.touchX = 1920;
        touchUp.touchY = 1080;

        retval.add(touchUp);

        return retval;
    }

    public static void main(String[] args) throws Exception {
        final ArrayList<ArrayList<TouchData>> strokes = new ArrayList<>();

        ArrayList<TouchData> stroke = generateStroke();
        strokes.add(stroke);

        Socket socket = new Socket("127.0.0.1", 9090);

        final InputStream inputStream = socket.getInputStream();
        final OutputStream outputStream = socket.getOutputStream();

        outputStream.write(16);
        outputStream.write(9);

        final ClientState clientState = new ClientState();

        final Thread listeningThread =
                new Thread(
                        new Runnable() {
                            public void run() {
                                try {
                                    int intBuffer = -1;
                                    byte receivedCommand;
                                    while (true) {
                                        intBuffer = inputStream.read();
                                        if (intBuffer < 0) {
                                            clientState.runningCommand = ServerCommands.STOP;
                                            System.out.println("Listening thread existing...");
                                            break;
                                        }

                                        receivedCommand = (byte) intBuffer;
                                        if (receivedCommand == ServerCommands.STOP) {
                                            clientState.runningCommand = ServerCommands.STOP;
                                        } else if (receivedCommand == ServerCommands.START) {
                                            clientState.runningCommand = ServerCommands.START;
                                        } else if (receivedCommand == ServerCommands.PAUSE) {
                                            clientState.runningCommand = ServerCommands.PAUSE;
                                        }
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace(System.err);
                                }
                            }
                        });

        listeningThread.start();

        final Thread sendingDataThread =
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    int strokeIdx = 0;
                                    int touchIdx = 0;

                                    while (true) {
                                        if (clientState.runningCommand == ServerCommands.STOP) {
                                            break;
                                        } else if (clientState.runningCommand
                                                == ServerCommands.START) {
                                            if (strokeIdx >= strokes.size()) {
                                                break;
                                            } else {
                                                if (touchIdx >= strokes.get(strokeIdx).size()) {
                                                    touchIdx = 0;
                                                    strokeIdx++;
                                                } else {
                                                    System.out.println(
                                                            "strokeIdx: "
                                                                    + Integer.toString(strokeIdx));
                                                    System.out.println(
                                                            "touchIdx: "
                                                                    + Integer.toString(touchIdx));
                                                    TouchData touchData =
                                                            strokes.get(strokeIdx).get(touchIdx);
                                                    outputStream.write(touchData.packData());
                                                    touchIdx++;
                                                }
                                            }
                                            // outputStream.write();
                                        } else if (clientState.runningCommand
                                                == ServerCommands.PAUSE) {
                                            while (clientState.runningCommand
                                                    == ServerCommands.PAUSE) {
                                                Thread.sleep(500);
                                            }
                                        }
                                    }

                                    listeningThread.interrupt();
                                } catch (Exception ex) {
                                    ex.printStackTrace(System.err);
                                }
                            }
                        });

        sendingDataThread.start();

        listeningThread.join();
        sendingDataThread.join();

        socket.close();
    }
}
