package io.github.ichisadashioko.jtouchinputserver;

public class Validator {
    public static boolean validatePortNumber(int port) {
        if (port < 0) {
            return false;
        }

        if (port > 65535) {
            return false;
        }

        return true;
    }

    public static int validatePortString(String s) {
        int port;

        try {
            port = Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            ex.printStackTrace(System.err);
            return -1;
        }

        boolean isValidPort = validatePortNumber(port);
        if (isValidPort) {
            return port;
        } else {
            return -1;
        }
    }
}
