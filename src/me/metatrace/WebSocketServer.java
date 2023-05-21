package me.metatrace;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class WebSocketServer {

    private static final int PORT = 8080;
    private static final Gson gson = new Gson();
    private static final Map<String, BigInteger> uniqueNumbers = new HashMap<>();
    private static final Map<String, SocketChannel> connections = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
        System.out.println("Server started on port " + PORT);

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            String ipAddress = socketChannel.socket().getInetAddress().getHostAddress();
            if (connections.containsKey(ipAddress)) {
                System.out.println("Connection from " + ipAddress + " rejected. Only one connection allowed per IP address.");
                socketChannel.close();
            } else {
                connections.put(ipAddress, socketChannel);
                System.out.println("Connection from " + ipAddress + " accepted.");

                // Generate a unique BigInteger number
                BigInteger number = generateUniqueNumber();

                // Convert the number to JSON
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("number", number.toString());
                String json = gson.toJson(jsonObject);

                // Send the JSON response to the client
                ByteBuffer buffer = ByteBuffer.wrap(json.getBytes());
                socketChannel.write(buffer);

                // Close the connection
                socketChannel.close();
                connections.remove(ipAddress);
            }
        }
    }

    private static BigInteger generateUniqueNumber() {
        BigInteger number = new BigInteger(64, new java.util.Random());
        while (uniqueNumbers.containsValue(number)) {
            number = new BigInteger(64, new java.util.Random());
        }
        uniqueNumbers.put(number.toString(), number);
        return number;
    }
}

