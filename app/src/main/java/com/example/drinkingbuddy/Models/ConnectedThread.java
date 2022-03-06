package com.example.drinkingbuddy.Models;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import android.os.Handler;

// This class has been inspired by an Arduino tutorial of a light switch
// Link of Tutorial: https://create.arduino.cc/projecthub/azoreanduino/simple-bluetooth-lamp-controller-using-android-and-arduino-aa2253
public class ConnectedThread extends Thread{
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    public static final int RESPONSE_MESSAGE = 10;
    Handler handler;

    // Connect Thread to Bluetooth Device
    public ConnectedThread(BluetoothSocket socket, Handler newHandler) {
        bluetoothSocket = socket;
        handler = newHandler;
        try{
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            outputStream.flush();
        }

        catch(Exception e) {
            Log.d("Problem","Something is Wrong");
        }
    }

    // Run Thread, waiting for Incoming Messages
    public void run(){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while(true) {
            try{
                String response = bufferedReader.readLine();
                Message newMessage = new Message();
                newMessage.what = RESPONSE_MESSAGE;
                newMessage.obj = response;
                handler.sendMessage(newMessage);
            }

            catch(Exception e) {
                Log.d("Problem","Something is Wrong");
                break;
            }
        }
    }

    // Sending a Message Whenever on is Available
    public void write(byte[] bytes){
        try{
            outputStream.write(bytes);
        }

        catch(Exception e) {
            Log.d("Problem","Something is Wrong");
        }
    }
}