package com.home.skiffdro.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.home.skiffdro.R;
import com.home.skiffdro.common.connections.BT;
import com.home.skiffdro.common.connections.Connection;
import com.home.skiffdro.common.connections.ConnectionEvent;
import com.home.skiffdro.common.connections.IConnection;
import com.home.skiffdro.databinding.FragmentMiniLatheBinding;
import com.home.skiffdro.models.ModelLathe;

public class MiniLathe extends Fragment  implements ConnectionEvent {
    FragmentMiniLatheBinding binding;
    IConnection con = null;

    public MiniLathe() {   }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        con = (IConnection) Connection.getInstance();
        con.addListener(MiniLathe.this);

        ModelLathe m = binding.getMMiniLathe();
        m.setScalesValX((double)con.getValX());
        m.setScalesValZ((double)con.getValZ());

        m.SetX0();
        m.SetZ0();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mini_lathe, container, false);
        binding.setMMiniLathe(new ModelLathe());
        return binding.getRoot();
    }

    @Override
    public void RefreshData() {
        ModelLathe m = binding.getMMiniLathe();
        m.setScalesValX((double)con.getValX());
        m.setScalesValZ((double)con.getValY()); ///эээ...эээ... так надо )))

        binding.invalidateAll();
    }

    //Установки привязок координат
    public void setScalesValX (double val) {binding.getMMiniLathe().setScalesValX(val);}
    public void setScalesValZ (double val) {binding.getMMiniLathe().setScalesValZ(val);}

    //Установка длины и диаметра
    public void setD(double ScalesDsetX, double ScasesDfixX){ binding.getMMiniLathe().setD(ScalesDsetX, ScasesDfixX); }
    public void setD(Bundle b) { setD(b.getDouble("ScalesDsetX"), b.getDouble("ScalesDfixX")); }
    public void setL(double ScalesLsetY, double ScasesLfixY){ binding.getMMiniLathe().setL(ScalesLsetY, ScasesLfixY); }

    //Получение привязанных данных
    public double getX() { return binding.getMMiniLathe().getXval();}
    public double getZ() { return binding.getMMiniLathe().getZval();}
    public double getD() { return binding.getMMiniLathe().getDval();}
    public double getL() { return binding.getMMiniLathe().getLval();}

    public void SetZasABS(boolean asABS) { binding.getMMiniLathe().ZasABS = asABS; }

    public void Refresh(){ binding.invalidateAll();  }
}