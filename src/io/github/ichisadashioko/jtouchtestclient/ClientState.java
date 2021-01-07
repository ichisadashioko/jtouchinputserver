package io.github.ichisadashioko.jtouchtestclient;

import io.github.ichisadashioko.jtouchinputserver.ServerCommands;

public class ClientState {
    public byte runningCommand;

    public ClientState() {
        this.runningCommand = ServerCommands.PAUSE;
    }
}
