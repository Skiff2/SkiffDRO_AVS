package com.home.skiffdro.lathe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import com.home.skiffdro.common.CenterSmoothScroller;
import com.home.skiffdro.common.ItemAdapter;
import com.home.skiffdro.common.Utils;
import com.home.skiffdro.fragments.MiniLathe;
import com.home.skiffdro.milling.MillingRoundDrill;
import com.home.skiffdro.models.ItemModel;

import java.util.ArrayList;

public class LathePulley extends AppCompatActivity implements BTEvent {
    private class Profile
    {
        public String Name;
        public float Deep;
        public float Step;
        public float Alpha;
    }

    ArrayList<ItemModel> states = new ArrayList<>();
    RecyclerView recyclerView;
    MiniLathe display;

    BT con = null;
    ArrayList<Profile> Profiles = new ArrayList<>();
    Profile SelProfile;
    int CntLines;

    TextView txtPulleyName, txtPulleyDeep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lathe_pulley);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        con = BT.getInstance();
        con.addListener(LathePulley.this);

        recyclerView = findViewById(R.id.recyclerView);
        txtPulleyName = (TextView) findViewById(R.id.txtPulleyName);
        txtPulleyDeep = (TextView) findViewById(R.id.txtPulleyDeep);

        Bundle arguments = getIntent().getExtras();
        display = (MiniLathe) getSupportFragmentManager().findFragmentById(R.id.fragmentDisplay);

        if(arguments!=null){ //Передача диаметра
            display.setD(arguments.getDouble("ScalesDsetX"), arguments.getDouble("ScalesDfixX"));
        }

        LoadProfiles();
        SelProfile();
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

                FillList();
            }
        });
        builderSingle.show();
    }

    private void FillList()
    {
        float val = 0;
        for (int i = 0; i < CntLines; i++) {
            states.add(new ItemModel(i+1,"Z" ,"", val, 0));
            val += SelProfile.Step;
        }

        ItemAdapter adapter = new ItemAdapter(this, states);
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void RefreshBTData() {
        try {
            for (int i = 0; i < states.size(); i++) {
                ItemModel m = states.get(i);

                if (display.getX() >= (SelProfile.Deep - 0.02) && Math.abs(m.getA() - display.getZ()) < 0.06) {
                    m.setCheck(true);
                }

                if (Math.abs(display.getZ() - m.getA()) < 0.06) {
                    m.setFoud(true);
                    Utils.SetRWPosition(recyclerView, i);
                }
                else
                    m.setFoud(false);

                recyclerView.getAdapter().notifyItemChanged(i);
            }
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        catch (Exception ex){}
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Выйти?")
                .setMessage("Вы действительно хотите выйти?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> LathePulley.super.onBackPressed()).create().show();
    }
}