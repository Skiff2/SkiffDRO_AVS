package com.home.skiffdro.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.home.skiffdro.common.Setts;

import java.io.Serializable;

public class SettsModel  extends BaseObservable {
    Setts s;

    boolean isPortret = true;
    boolean isLandscape = false;

    public SettsModel()
    {
        s = Setts.getInstance();
        isPortret = s.getIsPortret();
        isLandscape = !isPortret;
    }


    @Bindable
    public boolean getPortret(){return isPortret;}
    @Bindable
    public boolean getLandscape() {return isLandscape;}


    public void setPortret() {
        s.setIsPortret(true);
        isPortret = true;
        isLandscape = !isPortret;
        notifyPropertyChanged(BR._all);
    }

    public void setLandscape() {
        s.setIsPortret(false);
        isPortret = false;
        isLandscape = !isPortret;
        notifyPropertyChanged(BR._all);
    }



}
