package com.example.myapplication.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.R;
import com.example.myapplication.ui.home.HomeViewModel;

public class GalleryFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView tvZhendongData, tvDizhenDengji, tvBaojing;
    private Button btnBaojingOn, btnBaojingOff, btnShoudong, btnZidong;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        tvZhendongData = root.findViewById(R.id.zhendong_data);
        tvDizhenDengji = root.findViewById(R.id.dizhen_dengji);
        tvBaojing = root.findViewById(R.id.baojing);
        btnBaojingOn = root.findViewById(R.id.button_baojing_on);
        btnBaojingOff = root.findViewById(R.id.button_baojing_off);
        btnShoudong = root.findViewById(R.id.button_shoudong);
        btnZidong = root.findViewById(R.id.button_zidong);

        // 用 requireActivity() 保证与 HomeFragment 共享 ViewModel（即共享 TCP client）
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // 监听实时数据
        homeViewModel.getTcpData().observe(getViewLifecycleOwner(), this::updateSensorDisplay);

        // 控制按钮事件
        btnBaojingOn.setOnClickListener(v -> homeViewModel.sendCommand("buzer_on"));
        btnBaojingOff.setOnClickListener(v -> homeViewModel.sendCommand("buzer_of"));
        btnShoudong.setOnClickListener(v -> {
            homeViewModel.sendCommand("shoudong");
            btnZidong.setEnabled(true);
            btnShoudong.setEnabled(false);
        });
        btnZidong.setOnClickListener(v -> {
            homeViewModel.sendCommand("zhi_dong");
            btnZidong.setEnabled(false);
            btnShoudong.setEnabled(true);
        });

        return root;
    }

    // 数据解析与显示
    private void updateSensorDisplay(String receivedData) {
        float x = 0;
        int earthquake_grade = 0, buzzer_state = 0;
        String[] parts = receivedData.split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                try {
                    switch (key) {
                        case "x": x = Float.parseFloat(value); break;
                        case "earthquake_grade": earthquake_grade = Integer.parseInt(value); break;
                        case "buzzer_state": buzzer_state = Integer.parseInt(value); break;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        tvZhendongData.setText(String.format("%.1f", x));
        tvDizhenDengji.setText(String.valueOf(earthquake_grade));
        tvBaojing.setText(buzzer_state == 1 ? "开" : "关");
    }
}