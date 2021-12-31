package com.home.skiffdro.fragments;

import android.os.Bundle;

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

    public static LatheMain newInstance(String param1, String param2) {
        LatheMain fragment = new LatheMain();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lathe_main, container, false);
        binding.setMLathe(new ModelLathe());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        con = BT.getInstance();
        con.addListener(LatheMain.this);
        cmdSetD = (Button) getView().findViewById(R.id.cmdSetD);
        cmdSetL = (Button) getView().findViewById(R.id.cmdSetL);

        cmdSetD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new InputDialog(new InputDialog.DialogEvent() {
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
                }, getActivity());
            }
        });
        cmdSetL.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new InputDialog(new InputDialog.DialogEvent() {
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
                }, getActivity());
            }
        });
    }

    @Override
    public void RefreshBTData() {
        ModelLathe m = binding.getMLathe();
        m.setScalesValX((double) con.getValX());
        m.setScalesValZ((double) con.getValY());
        binding.invalidateAll();
    }
}