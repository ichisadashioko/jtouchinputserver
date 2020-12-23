# Communication protocol

This program will be the `SocketServer` and wait for clients to connect.

The client will first need to send some metadata information to server to identify the connected client.

We are not going to care about the device width and height information because touch position (x and y) is going to be stored in 2 bytes only and scaled between (0-255). Because of that we are going to retrieve the aspect ratio only. For example: 16x9 or 4:3

Client will need to send 2 bytes upon the connection is established. The client aspect ratio will be calculated by divide the first byte to the second byte. These two bytes must not be zeroes.

Now the client will wait for commands from the server.

The server can send one of these commands (stored in a single byte).

```
- Start/Resume: tell the client to start sending their touch input
- Pause: tell the client to pause sending their touch input
- Stop: tell the client to disconnect
```

Each touch input data will be packed into a 4 bytes package.

- The first byte stores the "ID" of the touch (for supporting multi-touch device).
- The second byte stores the "state" of the touch (down/move/up).
- The third byte stores the x-axis position of the touch. This value is calculated by this formula - `(touch_x_position / device_width) * 255` - and is rounded as a byte (`0-255`) value.
- The fourth byte stores the y-axis position of the touch. It is also used the above formula to calculate the value.

~~The client should starts a separated thread for reading data from the server with timeout. After the timeout, if there is no data then the client should continue their current job.~~

TODO deal with network timeout problem
