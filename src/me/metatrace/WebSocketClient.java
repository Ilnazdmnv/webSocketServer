package me.metatrace;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class WebSocketClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try {
            // Connect to the server
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT));

            // Receive the JSON response from the server
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            socketChannel.read(buffer);
            buffer.flip();
            String json = new String(buffer.array(), 0, buffer.limit());

            // Parse the JSON response
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            String numberString = jsonObject.get("number").getAsString();
            System.out.println("Received number: " + numberString);

            // Close the connection
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
