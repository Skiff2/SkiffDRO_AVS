package com.home.skiffdro.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.home.skiffdro.MainActivity;
import com.home.skiffdro.R;
import com.home.skiffdro.fragments.LatheMain;
import com.home.skiffdro.fragments.MillingMain;
import com.home.skiffdro.fragments.MiniMilling;
import com.home.skiffdro.models.MarkModel;

public class MarkDialog {
    public interface DialogEvent { void DialogOK(MarkModel mark); void  DialogCancel(); }

    private DialogEvent dEvent;
    ///////////////Далог
    public MarkDialog(DialogEvent dialogEvent, Context context, Object Model)
    {
        show(dialogEvent, context, Model);
    }

    void show (DialogEvent dialogEvent, Context context, Object Model) {
        //typeDialog = dType;
        // get prompts.xml view
        dEvent = dialogEvent;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.mark_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        final EditText txtA = (EditText) promptView.findViewById(R.id.txtA);
        final EditText txtB = (EditText) promptView.findViewById(R.id.txtB);
        final EditText txtC = (EditText) promptView.findViewById(R.id.txtC);

        final CheckBox chA = (CheckBox)  promptView.findViewById(R.id.chA);
        final CheckBox chB = (CheckBox)  promptView.findViewById(R.id.chB);
        final CheckBox chC = (CheckBox)  promptView.findViewById(R.id.chC);

        if (Model.getClass() == MillingMain.class)
        {
            MillingMain m = (MillingMain) Model;
            chA.setText("X:"); chB.setText("Y:"); chC.setText("Z:");
            txtA.setText(Utils.ValToPrint(m.getX())); txtB.setText(Utils.ValToPrint(m.getY())); txtC.setText(Utils.ValToPrint(m.getZ()));
        }
        else
        {
            LatheMain m = (LatheMain) Model;
            chA.setText("X:"); chB.setText("Z:"); chC.setVisibility(View.GONE); chC.setText("");
            txtA.setText(Utils.ValToPrint(m.getX())); txtB.setText(Utils.ValToPrint(m.getZ())); txtC.setVisibility(View.GONE);
        }

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", null)
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                dEvent.DialogCancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();

        alert.setOnShowListener(dialogInterface -> {
            Button button = ((AlertDialog) alert).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chA.isChecked() || chB.isChecked() || chC.isChecked()) {
                        MarkModel m = new MarkModel(String.valueOf(editText.getText()),
                                chA.isChecked()? chA.getText().toString():"",
                                chB.isChecked()? chB.getText().toString():"",
                                chC.isChecked()? chC.getText().toString():"",
                                Double.parseDouble(txtA.getText().toString().replace(",",".")),
                                Double.parseDouble(txtB.getText().toString().replace(",",".")),
                                Double.parseDouble(txtC.getText().toString().replace(",",".")));
                        dEvent.DialogOK(m);
                        alert.dismiss();
                    }
                    else
                        Toast.makeText(context.getApplicationContext(), "Не выбрана ни одна ось для метки!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        alert.show();
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }
}
