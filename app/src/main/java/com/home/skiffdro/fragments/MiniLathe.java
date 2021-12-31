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
import com.home.skiffdro.databinding.FragmentMiniLatheBinding;
import com.home.skiffdro.models.ModelLathe;

public class MiniLathe extends Fragment {
    FragmentMiniLatheBinding binding;

    public MiniLathe() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mini_lathe, container, false);
        binding.setMMiniLathe(new ModelLathe());
        return binding.getRoot();
    }


    public void setScalesValX (double val) {binding.getMMiniLathe().setScalesValX(val);}
    public void setScalesValZ (double val) {binding.getMMiniLathe().setScalesValZ(val);}

    public void Refresh(){ binding.invalidateAll();  }
}