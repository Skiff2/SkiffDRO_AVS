package com.home.skiffdro.lathe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.home.skiffdro.common.connections.BT;
import com.home.skiffdro.common.connections.ConnectionEvent;
import com.home.skiffdro.R;
import com.home.skiffdro.common.adapters.CenterSmoothScroller;
import com.home.skiffdro.common.adapters.ItemAdapter;
import com.home.skiffdro.common.Utils;
import com.home.skiffdro.fragments.MiniLathe;
import com.home.skiffdro.models.ItemModel;
import com.home.skiffdro.models.Setts;

import java.util.ArrayList;

public class LatheBoll extends AppCompatActivity implements ConnectionEvent, TextWatcher {
    ArrayList<ItemModel> states = new ArrayList<>();
    RecyclerView recyclerView;
    MiniLathe display;

    BT con = null;

    double ScalesDsetX = 0; //Установленный размер диаметра
    double ScalesDfixX = 0; //АБСОЛЮТНОЕ значение линейки для установленного диаметра

    TextView txtSteps, txtD, txtRWidth;

    private class vals
    {
        public double X;
        public double Y;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lathe_boll);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Setts.instance.getIsUseFullScreen())
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        con = BT.getInstance();
        con.addListener(LatheBoll.this);

        recyclerView = findViewById(R.id.recyclerView);

        Bundle arguments = getIntent().getExtras();
        display = (MiniLathe) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);

        if(arguments!=null){ //Передача диаметра
            ScalesDsetX = arguments.getDouble("ScalesDsetX");
            ScalesDfixX = arguments.getDouble("ScalesDfixX");
            display.setD(ScalesDsetX, ScalesDfixX);
        }

        txtRWidth = (TextView) findViewById(R.id.txtRWidth);
        txtSteps = (TextView) findViewById(R.id.txtSteps);
        txtD = (TextView) findViewById(R.id.txtD);

        txtSteps.addTextChangedListener(LatheBoll.this);
        txtRWidth.addTextChangedListener(LatheBoll.this);
        txtD.addTextChangedListener(LatheBoll.this);

    }

    @Override
    public void RefreshData() {
        try {
            if (states.size() > 0) {
                for (int i = 0; i < states.size(); i++) {
                    ItemModel m = states.get(i);

                    if (Math.abs(m.getA() - display.getZ()) <= 0.05) {
                        m.setFoud(true);
                        Utils.SetRWPosition(recyclerView, i);
                    } else
                        m.setFoud(false);

                    if (Math.abs(m.getB() - display.getD()) <= 0.1 && Math.abs(m.getA() - display.getZ()) <= 0.1) {
                        m.setCheck(true);
                    }
                    recyclerView.getAdapter().notifyItemChanged(i);
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
        catch (Exception ex){}
    }

    private void CalcTable()
    {
        if (txtD.getText().length() == 0 || txtSteps.getText().length() == 0 || txtRWidth.getText().length() == 0)
            return;

        states.clear();

        double d = Double.parseDouble(String.valueOf(txtD.getText()));
        int Stps = Integer.parseInt(String.valueOf(txtSteps.getText()));;
        double H = Double.parseDouble(String.valueOf(txtRWidth.getText()));;

        double r = d / 2;
        vals[] arr = new vals[Stps+2];

        int N = Stps / 2;

//        for (int i = 0; i < N + 1; i++)
//        {
//            vals v = new vals();
//            v.X = (r / (Stps / 2))* i;
//            v.Y = Math.sqrt((r * r) - (v.X * v.X)) * 2;
//            arr[i+N] = v;
//            arr[i + N].X += r;
//        }

        double sumX = 0;

        for (int i = 0; i < N ; i++)
        {
            vals v = new vals();

            //v.X = (r / (Stps / 2))* i;
            if (i < N / 2+1)
            {
                v.X = ((r / (Stps / 2)) * i) * 1.5;
                sumX = v.X;
            }
            else
            {
                v.X = sumX + ((r / (Stps / 2)) * (i + 1 - N / 2)) / 2;
            }
            v.Y = Math.sqrt((r * r) - (v.X * v.X)) * 2;
            arr[i+N] = v;
            arr[i + N].X += r;
        }

        for (int i = 0; i < N; i++)
        {
            vals v = new vals();
            v.X = d-arr[i + N].X;
            v.Y = arr[i + N].Y;
            arr[N - i] = v;

            arr[i + N].X += H;
        }

        for (int i = 0; i < Stps+1; i++) {
            if (arr[i] != null)
                states.add(new ItemModel(i,"Z" ,"D", arr[i].X, arr[i].Y));
         }

        ItemAdapter adapter = new ItemAdapter(this, states);
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            CalcTable();
        }
        catch (Exception ex)
        {
            Toast.makeText(LatheBoll.this, "Ошибка расчёта! Скорректируейте данные!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Выйти?")
                .setMessage("Вы действительно хотите выйти?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> LatheBoll.super.onBackPressed()).create().show();
    }
}