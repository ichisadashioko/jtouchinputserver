package io.github.ichisadashioko.jtouchinputserver;

public class Main {
    public static void main(String[] args) {
        Application app = new Application();
        ApplicationWindowListener windowListener = new ApplicationWindowListener(app);

        app.addWindowListener(windowListener);
        app.setVisible(true);

        System.out.println("after setVisible");
    }
}
