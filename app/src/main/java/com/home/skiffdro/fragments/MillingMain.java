package com.home.skiffdro.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.home.skiffdro.R;
import com.home.skiffdro.common.BT;
import com.home.skiffdro.common.BTEvent;
import com.home.skiffdro.databinding.FragmentMillingMainBinding;
import com.home.skiffdro.models.ModelMilling;

public class MillingMain extends Fragment implements BTEvent {
    FragmentMillingMainBinding binding;
    BT con = null;

    public MillingMain() {
    }

    public static MillingMain newInstance(String param1, String param2) {
        MillingMain fragment = new MillingMain();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_milling_main, container, false);
        binding.setMMilling(new ModelMilling());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        con = BT.getInstance();
        con.addListener(MillingMain.this);
    }

    @Override
    public void RefreshBTData() {
        ModelMilling m = binding.getMMilling();

        m.setScalesValX(con.getValX());
        m.setScalesValY(con.getValY());
        m.setScalesValZ(con.getValZ());

        binding.invalidateAll();
    }

    //Найдены ли центры?
    public boolean CenterXFound(){ return binding.getMMilling().X1Setted() && binding.getMMilling().X2Setted(); }
    public boolean CenterYFound(){ return binding.getMMilling().Y1Setted() && binding.getMMilling().Y2Setted(); }

    //Получение координат вычисленного центра
    public double CenterX() {
        return binding.getMMilling().CenterX;
    }
    public double CenterY() {
        return binding.getMMilling().CenterY;
    }

    //Добавляет в Интент данные по диаметру, если тот задан
    public void SetIntentCenter(Intent i)
    {
        if (CenterXFound() && CenterYFound())
        {
            i.putExtra("CenterX", CenterX());
            i.putExtra("CenterY", CenterY());
        }
    }
}
