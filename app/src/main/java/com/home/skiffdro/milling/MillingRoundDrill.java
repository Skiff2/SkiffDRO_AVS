package com.home.skiffdro.milling;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.TextView;

import com.home.skiffdro.common.BT;
import com.home.skiffdro.common.BTEvent;
import com.home.skiffdro.common.CenterSmoothScroller;
import com.home.skiffdro.common.InputDialog;
import com.home.skiffdro.common.ItemAdapter;
import com.home.skiffdro.fragments.MillingMain;
import com.home.skiffdro.fragments.MiniMilling;
import com.home.skiffdro.R;
import com.home.skiffdro.models.ItemModel;

import java.util.ArrayList;

public class MillingRoundDrill extends AppCompatActivity implements BTEvent {

    ArrayList<ItemModel> states = new ArrayList<>();
    RecyclerView recyclerView;
    MiniMilling display;

    double CenterX = 0;
    double CenterY = 0;

    double Radius; int Holes;
    BT con = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milling_round_drill);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        con = BT.getInstance();
        con.addListener(MillingRoundDrill.this);

        Bundle arguments = getIntent().getExtras();

        display = (MiniMilling) getSupportFragmentManager().findFragmentById(R.id.fragmentMiniCoordinate);
        display.HideResetX();
        display.HideResetY();

        if(arguments!=null){ //Позиционируемя относительно центра
            CenterX = arguments.getDouble("CenterX");
            CenterY = arguments.getDouble("CenterY");
            display.setScalesOffsetX(CenterX);
            display.setScalesOffsetY(CenterY);
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

    private void setInitialData(){
        double step = ((360 / Holes)* Math.PI) / 180 ;
        for (int i = 1; i <= Holes; i++) {
            double x = Math.round(Radius * Math.sin(step * i));
            double y = Radius * Math.cos(step * i);
            states.add(new ItemModel(i, "Y", x, y));
        }

        recyclerView = findViewById(R.id.recyclerView);
        ItemAdapter adapter = new ItemAdapter(this, states);
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void RefreshBTData() {
        try {
            for (int i = 0; i < states.size(); i++) {
                ItemModel m = states.get(i);
                if (Math.abs(m.getX() - display.getX()) < 0.05 && Math.abs(m.getS() - display.getY()) < 0.05) {
                    m.setFoud(true);
                    m.setCheck(true);

                    RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
                    RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
                    smoothScroller.setTargetPosition(i);
                    lm.startSmoothScroll(smoothScroller);
                } else
                    m.setFoud(false);
            }
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        catch(Exception ex)
        {}
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