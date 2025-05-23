package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // 修改这里，保证和GalleryFragment用法一致
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 按钮绑定
        binding.buttonConnect.setOnClickListener(v -> {
            String ip = binding.editIp.getText().toString();
            int port = Integer.parseInt(binding.editPort.getText().toString());
            homeViewModel.connect(ip, port);
        });
        binding.buttonDisconnect.setOnClickListener(v -> {
            homeViewModel.disconnect();
        });

        // 控制按钮监听
        binding.buttonBaojingOn.setOnClickListener(v -> homeViewModel.sendCommand("buzer_on"));
        binding.buttonBaojingOff.setOnClickListener(v -> homeViewModel.sendCommand("buzer_of"));
        binding.buttonShoudong.setOnClickListener(v -> {
            homeViewModel.sendCommand("shoudong");
            binding.buttonZidong.setEnabled(true);
            binding.buttonShoudong.setEnabled(false);
        });
        binding.buttonZidong.setOnClickListener(v -> {
            homeViewModel.sendCommand("zhi_dong");
            binding.buttonZidong.setEnabled(false);
            binding.buttonShoudong.setEnabled(true);
        });

        // 状态监听
        homeViewModel.getTcpStatus().observe(getViewLifecycleOwner(),
                status -> binding.textStatus.setText(status));

        // 数据监听
        homeViewModel.getTcpData().observe(getViewLifecycleOwner(), data -> {
            binding.textData.setText(data); // 显示原始数据
            updateSensorDisplay(data);      // 显示到三个变量
            homeViewModel.handleIncomingData(data); // 这里存数据库
        });

        return root;
    }

    // 解析数据并显示到三个变量
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
        binding.zhendongData.setText(String.format("%.1f", x));
        binding.dizhenDengji.setText(String.valueOf(earthquake_grade));
        binding.baojing.setText(buzzer_state == 1 ? "开" : "关");
    }
}