package com.example.myapplication.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.myapplication.ui.home.TcpClient;
import com.example.myapplication.SensorData;
import com.example.myapplication.SensorDataRepository;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
public class HomeViewModel extends AndroidViewModel {
    private MutableLiveData<String> tcpStatus = new MutableLiveData<>();
    private MutableLiveData<String> tcpData = new MutableLiveData<>();
    private TcpClient client;

    public LiveData<String> getTcpStatus() { return tcpStatus; }
    public LiveData<String> getTcpData() { return tcpData; }
    public void sendCommand(String cmd) {
        if (client != null) {
            client.send(cmd);
        }
    }
    public void connect(String ip, int port) {
        client = new TcpClient(new TcpClient.OnConnectionListener() {
            @Override
            public void onConnected() {
                tcpStatus.postValue("已连接");
            }

            @Override
            public void onConnectionFailed(Exception e) {
                tcpStatus.postValue("连接失败: " + e.getMessage());
            }

            @Override
            public void onDisconnected() {
                tcpStatus.postValue("已断开");
            }

            @Override

            public void onMessage(String message) {
                tcpData.postValue(message);
                handleIncomingData(message); // 自动存库
            }
        });
        client.connect(ip, port);
        tcpStatus.postValue("连接中...");
    }

    public void disconnect() {
        if (client != null) client.disconnect();
        // tcpStatus 会在 onDisconnected 里自动变
    }
    private SensorDataRepository repository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new SensorDataRepository(application);
    }
    // 解析数据并存数据库
    public void handleIncomingData(String receivedData) {
        // 解析字符串
        float x = 0;
        int earthquake_grade = 0, buzzer_state = 0;
        float ax_g = 0, ay_g = 0, az_g = 0;
        String[] parts = receivedData.split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                try {
                    switch (key) {
                        case "x": x = Float.parseFloat(value); break;
                        case "earthquake_grade": earthquake_grade = Integer.parseInt(value); break;
                        case "buzzer_state": buzzer_state = Integer.parseInt(value); break;
                        case "ax_g": ax_g = Float.parseFloat(value); break;
                        case "ay_g": ay_g = Float.parseFloat(value); break;
                        case "az_g": az_g = Float.parseFloat(value); break;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        long timestamp = System.currentTimeMillis();
        // 存数据库
        SensorData data = new SensorData(timestamp, x, earthquake_grade, buzzer_state, ax_g, ay_g, az_g);
        repository.insert(data);
    }
}