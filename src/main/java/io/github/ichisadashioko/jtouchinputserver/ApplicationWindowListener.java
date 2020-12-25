package io.github.ichisadashioko.jtouchinputserver;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ApplicationWindowListener implements WindowListener {
    public Application application;

    public ApplicationWindowListener(Application application) {
        this.application = application;
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        this.application.dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}
