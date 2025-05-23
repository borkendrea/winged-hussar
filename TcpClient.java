package com.example.myapplication.ui.home;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import android.util.Log;
public class TcpClient {
    public interface OnConnectionListener {
        void onConnected();
        void onConnectionFailed(Exception e);
        void onDisconnected();
        void onMessage(String message);
    }
    private OnConnectionListener listener;

    public TcpClient(OnConnectionListener listener) {
        this.listener = listener;
    }
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread receiveThread;
    private boolean running = false;

    public interface OnMessageReceived {
        void onMessage(String message);
    }





    // 连接服务器
    public void connect(String ip, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(ip, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                running = true;
                if (listener != null) listener.onConnected();

                receiveThread = new Thread(() -> {
                    String line;
                    try {
                        while (running && (line = in.readLine()) != null) {
                            if (listener != null) listener.onMessage(line);
                        }
                    } catch (IOException ignored) {}
                });
                receiveThread.start();
            } catch (IOException e) {
                if (listener != null) listener.onConnectionFailed(e);
            }
        }).start();
    }

    // 断开连接
    public void disconnect() {
        running = false;
        try {
            if (socket != null) socket.close();
            if (listener != null) listener.onDisconnected();
        } catch (IOException ignored) {}
    }

    public void send(String msg) {
        new Thread(() -> {
            try {
                Log.d("TcpClient", "Sending: " + msg);
                if (out != null) {
                    out.print(msg);
                    out.flush();
                } else {
                    Log.e("TcpClient", "Output stream is null, cannot send message!");
                }
            } catch (Exception e) {
                Log.e("TcpClient", "Send error: " + e.getMessage(), e);
            }
        }).start();
    }
}