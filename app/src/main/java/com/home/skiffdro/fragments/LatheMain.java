package com.home.skiffdro.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.home.skiffdro.R;
import com.home.skiffdro.common.BT;
import com.home.skiffdro.common.BTEvent;
import com.home.skiffdro.common.InputDialog;
import com.home.skiffdro.databinding.FragmentLatheMainBinding;
import com.home.skiffdro.models.ModelLathe;

public class LatheMain extends Fragment implements BTEvent {
    FragmentLatheMainBinding binding;

    Button  cmdSetD, cmdSetL;
    BT con = null;

    public LatheMain() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lathe_main, container, false);
        binding.setMLathe(new ModelLathe());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        con = BT.getInstance();
        con.addListener(LatheMain.this);
        cmdSetD = (Button) getView().findViewById(R.id.cmdSetD);
        cmdSetL = (Button) getView().findViewById(R.id.cmdSetL);

        //Установка диаметра
        cmdSetD.setOnClickListener(v -> new InputDialog(new InputDialog.DialogEvent() {
            @Override
            public void DialogOK(String Text) {
                if (Text.length() == 0)
                    binding.getMLathe().setD(0);
                else {
                    binding.getMLathe().setD(Float.parseFloat(Text));
                }
            }

            @Override
            public void DialogCancel() { binding.getMLathe().setD(0);  }
        }, getActivity()));
        //Установка Длины
        cmdSetL.setOnClickListener(v -> new InputDialog(new InputDialog.DialogEvent() {
            @Override
            public void DialogOK(String Text) {
                if (Text.length() == 0)
                    binding.getMLathe().setL(0);
                else {
                    binding.getMLathe().setL(Float.parseFloat(Text));
                }
            }

            @Override
            public void DialogCancel() {
                binding.getMLathe().setL(0);
            }
        }, getActivity()));
    }

    @Override
    public void RefreshBTData() {
        ModelLathe m = binding.getMLathe();
        m.setScalesValX((double) con.getValX());
        m.setScalesValZ((double) con.getValY());
        binding.invalidateAll();
    }

    //Установлены ли абслютные размеры?
    public boolean DSetted() {return binding.getMLathe().DSetted();}
    public boolean LSetted() {return binding.getMLathe().LSetted();}

    //Установленный размер диаметра
    public double getScalesDsetX() { return binding.getMLathe().ScalesDsetX; }
    //АБСОЛЮТНОЕ значение линейки для установленного диаметра
    public double getScalesDfixX() { return binding.getMLathe().ScalesDsetX; }

    //Установленный размер длины
    public double getScalesLsetX() { return binding.getMLathe().ScalesLsetZ; }
    //АБСОЛЮТНОЕ значение линейки для установленной длины
    public double getScalesLfixX() { return binding.getMLathe().ScalesLsetZ; }

    //Получение привязанных координат
    public double getX() { return binding.getMLathe().getX();}
    public double getZ() { return binding.getMLathe().getZ();}
}