package com.home.skiffdro.lathe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.home.skiffdro.common.BT;
import com.home.skiffdro.common.BTEvent;
import com.home.skiffdro.R;
import com.home.skiffdro.common.Utils;

import java.util.ArrayList;

public class LathePulley extends AppCompatActivity implements BTEvent {
    private class Profile
    {
        public String Name;
        public float Deep;
        public float Step;
        public float Alpha;
    }

    BT con = null;
    ArrayList<Profile> Profiles = new ArrayList<>();
    Profile SelProfile;
    int CntLines;

    double ScalesValX = 0; //текущее АБСОЛЮТНОЕ значение линейки
    double ScalesOffsetX = 0; //Значение локального обнуления
    double ScalesValY = 0; //текущее АБСОЛЮТНОЕ значение линейки
    double ScalesOffsetY = 0; //Значение локального обнуления

    TextView lblX, lblY, txtPulleyName, txtPulleyDeep;
    Button cmdResetX,  cmdResetY;
    ListView PulleySteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lathe_pulley);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        con = BT.getInstance();
        con.addListener(LathePulley.this);

        txtPulleyName = (TextView) findViewById(R.id.txtPulleyName);
        txtPulleyDeep = (TextView) findViewById(R.id.txtPulleyDeep);
        lblX = (TextView) findViewById(R.id.lblX);
        lblY = (TextView) findViewById(R.id.lblY);
        cmdResetX = (Button) findViewById(R.id.cmdResetX);
        cmdResetY = (Button) findViewById(R.id.cmdResetY);
        PulleySteps = (ListView) findViewById(R.id.PulleySteps);

        LoadProfiles();
        SelProfile();

        ResetVals();

        cmdResetX.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ScalesOffsetX = ScalesValX;
                for (int i = 0; i < PulleySteps.getCount(); i++)
                    PulleySteps.setItemChecked(i, false);
            }
        });
        cmdResetY.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ScalesOffsetY = ScalesValY;
            }
        });
    }

    private void LoadProfiles()
    {
        AddProfile("PH",2.5f, 1.6f,40);
        AddProfile("PJ",4.0f, 2.34f,40);
        AddProfile("PK",6.5f, 3.56f,40);
        AddProfile("PL",9.0f, 4.7f,40);
        AddProfile("PM",18, 9.4f,40);
    }

    private void AddProfile(String Name, float Deep, float Step, float Alpha)
    {
        Profile p = new Profile();
        p.Name = Name;
        p.Deep = Deep;
        p.Step = Step;
        p.Alpha = Alpha;

        Profiles.add(p);
    }


    private void SelProfile() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(LathePulley.this);
        builderSingle.setTitle("Выберите тип профиля:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(LathePulley.this, android.R.layout.select_dialog_singlechoice);
        for (Profile p: Profiles) {
            arrayAdapter.add(p.Name);
        }

        builderSingle.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                for (Profile p : Profiles) {
                    if (strName.equals(p.Name)){
                        SelProfile = p; SelCntLines(); break;
                    }
                }
            }
        });
        builderSingle.show();
    }
    private void SelCntLines() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(LathePulley.this);
        builderSingle.setTitle("Кол-во линий:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(LathePulley.this, android.R.layout.select_dialog_singlechoice);
        for (int i = 1; i < 21; i++) {
            arrayAdapter.add(String.valueOf(i));
        }

        builderSingle.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                CntLines = Integer.parseInt(strName);

                txtPulleyName.setText(strName + SelProfile.Name);
                txtPulleyDeep.setText("Угол резца: " + SelProfile.Alpha+ "\r\nГлубина реза: " + Utils.ValToPrint(SelProfile.Deep));

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(LathePulley.this, android.R.layout.simple_list_item_multiple_choice)
                {
                    public View getView(int position, View convertView, ViewGroup parent){
                        TextView item = (TextView) super.getView(position,convertView,parent);
                        item.setTextColor(Color.parseColor("#FF3E80F1"));
                        item.setTypeface(item.getTypeface(), Typeface.BOLD);
                        item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,26);
                        return item;
                    }
                };
                int bTotal = 0;
                float val = 0;
                for (int i = 0; i < CntLines; i++) {
                    adapter.add((i+1) + ") " + String.format("%.2f", val));
                    val += SelProfile.Step;
                }
                PulleySteps.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                PulleySteps.setAdapter(adapter);

                ResetVals();
            }
        });
        builderSingle.show();
    }

    private void ResetVals()
    {
        ScalesOffsetX = ScalesValX; //Сброс Х
        ScalesOffsetY = ScalesValY; //Сброс Y
    }

    @Override
    public void RefreshBTData() {
        ScalesValX = con.getValX();
        ScalesValY = con.getValY();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < PulleySteps.getCount(); i++) {
                    TextView vv = (TextView) PulleySteps.getAdapter().getView(i, null, null);
                    String s = vv.getText().toString();
                    s = s.substring(s.indexOf(" ") + 1).replace(",", ".");
                    if ((ScalesValX - ScalesOffsetX) >= (SelProfile.Deep-0.02)
                        && Math.abs(Double.parseDouble(s) - (ScalesValY - ScalesOffsetY)) < 0.06) {
                        PulleySteps.setItemChecked(i, true);
                    }

                    if (Math.abs(Double.parseDouble(s) - (ScalesValY - ScalesOffsetY)) < 0.06)
                        PulleySteps.getChildAt(i).setBackgroundColor(Color.YELLOW);
                    else
                        PulleySteps.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }

                lblX.setText(Utils.ValToPrint(Math.abs(ScalesValX - ScalesOffsetX)));
                lblY.setText(Utils.ValToPrint((ScalesValY - ScalesOffsetY)));
            }
        });

    }
}