package com.home.skiffdro.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.home.skiffdro.R;
import com.home.skiffdro.common.BT;
import com.home.skiffdro.common.BTEvent;
import com.home.skiffdro.databinding.FragmentMiniMillingBinding;
import com.home.skiffdro.models.ModelMilling;


public class MiniMilling extends Fragment implements BTEvent {
    FragmentMiniMillingBinding binding;
    BT con = null;

    public MiniMilling() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        con = BT.getInstance();
        con.addListener(MiniMilling.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mini_milling, container, false);
        binding.setMMilling(new ModelMilling());
        return binding.getRoot();
    }

    @Override
    public void RefreshBTData() {
        ModelMilling m = binding.getMMilling();

        m.setScalesValX(con.getValX());
        m.setScalesValY(con.getValY());
        m.setScalesValZ(con.getValZ());

        binding.invalidateAll();
    }

    //Методы для прятания кнопок
    public void HideResetX(){binding.getMMilling().setShowResetX(false);}
    public void HideResetY(){binding.getMMilling().setShowResetY(false);}

    //Внешняя установка нулей
    public void setScalesOffsetX(double val){binding.getMMilling().ScalesOffsetX = val; }
    public void setScalesOffsetY(double val){binding.getMMilling().ScalesOffsetY = val; }
    public void setScalesOffsetZ(double val){binding.getMMilling().ScalesOffsetZ = val; }
}