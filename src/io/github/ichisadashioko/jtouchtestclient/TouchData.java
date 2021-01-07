package io.github.ichisadashioko.jtouchtestclient;

public class TouchData {
    public int deviceWidth;
    public int deviceHeight;

    public int touchId;
    public byte touchEventType;
    public int touchX;
    public int touchY;

    public byte[] packData() {
        byte firstByte = (byte) (((this.touchId & 0b1111) << 4) + this.touchEventType);

        float scaledX_float = ((float) (this.touchX * 256)) / ((float) this.deviceWidth);
        int scaledX_int = Math.round(scaledX_float);

        float scaledY_float = ((float) (this.touchY * 256)) / ((float) this.deviceHeight);
        int scaledY_int = Math.round(scaledY_float);

        if ((scaledX_int == 0) && (scaledY_int == 0)) {
            byte buffer[] = {firstByte};
            return buffer;
        } else if ((scaledX_int == 0) && (scaledY_int != 0)) {
            firstByte = (byte) (firstByte | 0b1000);
            byte scaledY_byte = (byte) (scaledY_int - 1);
            byte[] buffer = {firstByte, scaledY_byte};
            return buffer;
        } else if ((scaledX_int != 0) && (scaledY_int == 0)) {
            firstByte = (byte) (firstByte | 0b100);
            byte scaledX_byte = (byte) (scaledX_int - 1);
            byte[] buffer = {firstByte, scaledX_byte};
            return buffer;
        } else {
            firstByte = (byte) (firstByte | 0b1100);
            byte scaledX_byte = (byte) (scaledX_int - 1);
            byte scaledY_byte = (byte) (scaledY_int - 1);
            byte[] buffer = {firstByte, scaledX_byte, scaledY_byte};
            return buffer;
        }
    }
}
