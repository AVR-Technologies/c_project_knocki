package com.example.knockiclient;
import android.os.StrictMode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    Client(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    OnMessage onMessage;
    Socket socket;
    Thread thread;
    private BufferedReader input;
    private PrintWriter output;

    void connect(String ip, int port, OnMessage _onMessage) {
        onMessage = _onMessage;
        try {
            socket = new Socket(InetAddress.getByName(ip), port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());
            thread = new Thread(read);
            thread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void close() {
        try {
            if(isConnected()) socket.close();
            if(input!=null) input.close();
            if(output!=null) output.close();
            thread = null;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void write(String message){
        if(isConnected() && output != null){
            output.write(message);
            output.flush();
        }
    }
    boolean isConnected() {
        return socket != null && socket.isConnected();
    }
    private final Runnable read = () -> {
        while (socket.isConnected())
            try {
                final String message = input.readLine();
                if (message != null) onMessage.received(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
    };
    public interface OnMessage {
        void received(String message);
    }
}