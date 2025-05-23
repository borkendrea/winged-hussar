package com.example.myapplication.ui.slideshow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DynamicLineChartView extends View {
    private static final int MAX_POINTS = 20;
    private List<Float> yData = new ArrayList<>();
    private List<Long> timeData = new ArrayList<>();
    private float yMin = 0, yMax = 5; // 初始地震等级范围
    private Paint axisPaint, linePaint, pointPaint;
    private TextPaint labelPaint;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public DynamicLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(3f);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(4f);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.RED);

        labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(28f);
    }

    public void addData(float y, long timestamp) {
        if (yData.size() >= MAX_POINTS) {
            yData.remove(0);
            timeData.remove(0);
        }
        yData.add(y);
        timeData.add(timestamp);
        // 动态调整Y轴
        float localMax = yData.stream().max(Float::compare).orElse(5f);
        if (localMax > yMax) yMax = localMax + 1;
        else if (localMax < yMax - 2) yMax = Math.max(localMax + 1, 5);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();
        int left = 100, bottom = h - 60, top = 60, right = w - 40;

        // 画坐标轴
        canvas.drawLine(left, top, left, bottom, axisPaint); // Y轴
        canvas.drawLine(left, bottom, right, bottom, axisPaint); // X轴

        // Y轴分度与标签
        int ySteps = 5;
        float yStep = (yMax - yMin) / ySteps;
        for (int i = 0; i <= ySteps; i++) {
            float yv = yMin + i * yStep;
            int yPos = bottom - (int)((yv - yMin) / (yMax - yMin) * (bottom - top));
            canvas.drawLine(left - 10, yPos, left, yPos, axisPaint);
            canvas.drawText(String.format(Locale.getDefault(), "%.0f", yv), left - 70, yPos + 8, labelPaint);
        }

        // X轴标签和折线
        int n = yData.size();
        if (n < 2) return;
        float xStep = (right - left) * 1f / (MAX_POINTS - 1);

        // 画折线和点
        for (int i = 1; i < n; i++) {
            float x1 = left + (i - 1) * xStep;
            float x2 = left + i * xStep;
            float y1 = bottom - (yData.get(i - 1) - yMin) / (yMax - yMin) * (bottom - top);
            float y2 = bottom - (yData.get(i) - yMin) / (yMax - yMin) * (bottom - top);
            canvas.drawLine(x1, y1, x2, y2, linePaint);
            canvas.drawCircle(x1, y1, 6, pointPaint);
            if (i == n - 1) canvas.drawCircle(x2, y2, 6, pointPaint);
        }

        // 画时间标签（每隔几个点画一次，避免重叠）
        int labelInterval = Math.max(1, n / 5);
        for (int i = 0; i < n; i += labelInterval) {
            float x = left + i * xStep;
            String label = timeFormat.format(new Date(timeData.get(i)));
            canvas.drawText(label, x - 40, bottom + 35, labelPaint);
        }
    }
}