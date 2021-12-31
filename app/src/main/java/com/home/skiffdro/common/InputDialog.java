package com.home.skiffdro.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.home.skiffdro.R;

public class InputDialog {
    public interface DialogEvent { void DialogOK(String Text); void  DialogCancel(); }

    private DialogEvent dEvent;
    ///////////////Далог
    public InputDialog (DialogEvent dialogEvent, Context context)
    {
        show(dialogEvent, context, "Задайте новое значение:",0);
    }

    public InputDialog (String Text, int InputType, DialogEvent dialogEvent, Context context) {
        show(dialogEvent, context, Text, InputType);
    }

    void show (DialogEvent dialogEvent, Context context, String Text, int InputType) {
        //typeDialog = dType;
        // get prompts.xml view
        dEvent = dialogEvent;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        editText.setHint(Text);
        if (InputType != 0) editText.setInputType(InputType);
        //InputType.TYPE_CLASS_NUMBER
        //promptView.setFocusableInTouchMode(true);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dEvent.DialogOK(String.valueOf(editText.getText()));
                    }
                })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                dEvent.DialogCancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }
}
