package com.home.skiffdro.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.home.skiffdro.MainActivity;
import com.home.skiffdro.common.Setts;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Setts s = Setts.getInstance(context);
        if (s.getIsAutostart()) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}

