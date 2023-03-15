package com.home.skiffdro.milling;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.home.skiffdro.common.connections.BT;
import com.home.skiffdro.common.connections.ConnectionEvent;
import com.home.skiffdro.common.adapters.CenterSmoothScroller;
import com.home.skiffdro.common.dialogs.InputDialog;
import com.home.skiffdro.common.adapters.ItemAdapter;
import com.home.skiffdro.common.Utils;
import com.home.skiffdro.fragments.MiniMilling;
import com.home.skiffdro.R;
import com.home.skiffdro.models.ItemModel;
import com.home.skiffdro.models.Setts;

import java.util.ArrayList;

public class MillingRoundDrill extends AppCompatActivity implements ConnectionEvent {

    ArrayList<ItemModel> states = new ArrayList<>();
    RecyclerView recyclerView;
    MiniMilling display;

    double CenterX = 0;
    double CenterY = 0;
    double PrevZ = 0;

    double Radius, StAngle; int Holes;
    BT con = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milling_round_drill);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Setts.instance.getIsUseFullScreen())
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        InitDispaly();

        con = BT.getInstance();
        con.addListener(MillingRoundDrill.this);

        Bundle arguments = getIntent().getExtras();
        if(arguments!=null) { //Позиционируемя относительно центра
            CenterX = arguments.getDouble("CenterX");
            CenterY = arguments.getDouble("CenterY");
        }

        new InputDialog("Радиус сверления", 0, new InputDialog.DialogEvent() {
            @Override
            public void DialogOK(String Text) {
                if (Text.length() == 0)
                    finish();
                else {
                    Radius = Double.parseDouble(Text);

                    TextView t = (TextView)findViewById(R.id.txtInfo);
                    t.setText("Отверстий: " + Holes + "  По радиусу: " + Radius);

                    setInitialData();
                }
            }

            @Override
            public void DialogCancel() {
                finish();
            }
        }, this);
        new InputDialog("Угол начала сверления", 0, new InputDialog.DialogEvent() {
            @Override
            public void DialogOK(String Text) {
                if (Text.length() == 0)
                    StAngle = 0;
                else
                    StAngle = Double.parseDouble(Text);
            }
            @Override
            public void DialogCancel() {
                StAngle = 0;
            }
        }, this);
        new InputDialog("Количество отверстий", InputType.TYPE_CLASS_NUMBER, new InputDialog.DialogEvent() {
            @Override
            public void DialogOK(String Text) {
                if (Text.length() == 0)
                    finish();
                else {
                    Holes = Integer.parseInt(Text);
                }
            }

            @Override
            public void DialogCancel() {
                finish();
            }
        }, this);

        con = BT.getInstance();
        con.addListener(MillingRoundDrill.this);
    }

    private void InitDispaly() {
        boolean IsPortret = Setts.instance.getIsPortret();

        if (IsPortret) {
            this.getSupportFragmentManager().beginTransaction()
                    .add(R.id.mfrPortret, MiniMilling.class, null)
                    .commit();

            ((LinearLayout) findViewById(R.id.llMarks)).setWeightSum(1);
            ((LinearLayout) findViewById(R.id.mfrLandscape)).setVisibility(View.GONE);
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mfrLandscape, MiniMilling.class, null)
                    .commit();
        }
    }

    private void setInitialData(){
        try {
            double AddAngl = (StAngle * Math.PI) / 180;
            double step = ((360 / Holes) * Math.PI) / 180;
            for (int i = 1; i <= Holes; i++) {
                double x = Radius * Math.sin(AddAngl + (step * i));
                double y = Radius * Math.cos(AddAngl + (step * i));
                states.add(new ItemModel(i, "X", "Y", x, y));
            }
        }
        catch (Exception ex){} //Ввели хрень - получили хрень.

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        ItemAdapter adapter = new ItemAdapter(this, states);
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void RefreshData() {
        InitDisplay();

        try {
            for (int i = 0; i < states.size(); i++) {
                ItemModel m = states.get(i);
                if (Math.abs(m.getA() - display.getX()) < 0.05 && Math.abs(m.getB() - display.getY()) < 0.05) {
                    m.setFoud(true);

                    if (Math.abs(PrevZ - display.getZ()) > 0.05)
                        m.setCheck(true);

                    Utils.SetRWPosition(recyclerView, i);
                } else
                    m.setFoud(false);
                recyclerView.getAdapter().notifyItemChanged(i);
            }
            recyclerView.getAdapter().notifyDataSetChanged();
            PrevZ = display.getZ();
        }
        catch(Exception ex)
        {}
    }

    private void InitDisplay()
    {
        if (display != null) return;

        boolean IsPortret = Setts.instance.getIsPortret();
        if (IsPortret)
            display = (MiniMilling) this.getSupportFragmentManager().findFragmentById(R.id.mfrPortret);
        else
            display =  (MiniMilling)getSupportFragmentManager().findFragmentById(R.id.mfrLandscape);

        if (display == null) return;

        display.HideResetX();
        display.HideResetY();

        display.setScalesOffsetX(CenterX);
        display.setScalesOffsetY(CenterY);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Выйти?")
                .setMessage("Вы действительно хотите выйти?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> MillingRoundDrill.super.onBackPressed()).create().show();
    }
}