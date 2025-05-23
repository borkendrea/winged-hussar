package com.example.myapplication;

// 数据类，用于存储每条记录
public class SensorData {
    public long timestamp;
    public float x;
    public int earthquakeGrade;
    public int buzzerState;
    public float ax_g, ay_g, az_g;

    public SensorData(long timestamp, float x, int earthquakeGrade, int buzzerState, float ax_g, float ay_g, float az_g) {
        this.timestamp = timestamp;
        this.x = x;
        this.earthquakeGrade = earthquakeGrade;
        this.buzzerState = buzzerState;
        this.ax_g = ax_g;
        this.ay_g = ay_g;
        this.az_g = az_g;
    }
}