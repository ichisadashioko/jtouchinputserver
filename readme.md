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

## Each touch input data will be packed into a 1-3 bytes package.

__The 1st byte__

```
87654321
│││││││└──── the touch state (0 - the finger is on the device surface
│││││││      before. 1 - the finger is just laid on the device surface.)
||||||└───── is the x-axis data greater than 0 (to deal with one off
||||||       error and reduce bandwidth)
|||||└────── is the y-axis data greater than 0
└┴┴┴┴─────── the touch ID (support up to 32 unique touches)
```

If the x-axis data bit in the 1st byte (the 3nd bit) is set to 1 then there is a following byte to store the x-axis data.

If the y-axis data bit in the 1st byte (the 4rd bit) is set to 1 then there is a following byte to store the y-axis data.

If both the x-axis bit and the y-axis bit are both set to 1 then the byte which stores x-axis data, will come before the byte which stores the y-axis data.

If the touch's axes data is not equal 0 then they are calculated using this formula:

```
x_axis = (byte) (((x_position * 256) / device_width) - 1)
y_axis = (byte) (((y_position * 256) / device_height) - 1)
```

Example:

```
The device has dimension of 640-pixel wide and 480-pixel tall. The aspect ratio is 4 by 3. The client will need to send two bytes `0x04` and `0x03` upon connecting to the server. After that, the client will enter idle state and wait for commands from the server. If the server sends the `Start/Resume` command (`0x01`) then the client can send the input information to the server.

There is a finger on the device surface at location `(320, 240)`. The position is not at `(0, 0)` so we need to convert at into two bytes values.

x_axis_pos = ((320 / 640) * 256) - 1
x_axis_pos = 127
x_axis_pos = 0x7f

y_axis_pos = ((240 / 480) * 256) - 1
y_axis_pos = 127
y_axis_pos = 0x7f

We will identify this finger with `ID` = `0`.

Finally, the client will need to send these three bytes - `0x03` `0x7f` `0x7f`.
```
