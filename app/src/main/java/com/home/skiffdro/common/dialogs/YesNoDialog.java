package com.home.skiffdro.common.dialogs;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class YesNoDialog {

    public interface DialogEvent { void DialogYes(); void  DialogNo(); }

    private YesNoDialog.DialogEvent dEvent;
    ///////////////Далог
    public YesNoDialog (YesNoDialog.DialogEvent dialogEvent, Context context, String Title, String Text)
    {
        dEvent = dialogEvent;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(Title);
        builder.setMessage(Text);

        builder.setPositiveButton("Да", (dialog, which) -> {
            dEvent.DialogYes();
            dialog.dismiss();
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> {
            dEvent.DialogNo();
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
