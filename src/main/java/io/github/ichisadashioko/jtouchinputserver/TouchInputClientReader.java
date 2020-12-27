package io.github.ichisadashioko.jtouchinputserver;

import java.io.InputStream;

public class TouchInputClientReader extends Thread {

    public InputStream inputStream;
    public TouchInputClientReaderEventListener listener;

    public TouchInputClientReader(InputStream inputStream) {
        super();

        this.inputStream = inputStream;
        this.listener = null;
    }

    public void run() {
        try {
            byte[] buffer = new byte[4];
            int idx = 0;
            int intBuffer = -1;

            while (true) {
                intBuffer = this.inputStream.read();

                if ((intBuffer < 0) || (intBuffer > 255)) {
                    throw new Exception("Invalid byte data! " + intBuffer);
                }

                buffer[idx] = (byte) intBuffer;
                idx++;
                if (idx > 3) {
                    idx = 0;
                    if (this.listener != null) {
                        this.listener.onNewTouchInputData(buffer);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
