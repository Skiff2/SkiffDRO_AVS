package com.home.skiffdro.lathe;

import static com.home.skiffdro.common.Utils.ValToPrint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;

import com.home.skiffdro.common.BT;
import com.home.skiffdro.common.BTEvent;
import com.home.skiffdro.R;
import com.home.skiffdro.common.CenterSmoothScroller;
import com.home.skiffdro.common.ItemAdapter;
import com.home.skiffdro.common.Utils;
import com.home.skiffdro.databinding.ActivityLatheThreadBinding;
import com.home.skiffdro.fragments.MiniLathe;
import com.home.skiffdro.models.ItemModel;
import com.home.skiffdro.models.ModelLathe;

import java.util.ArrayList;

public class LatheThread extends AppCompatActivity implements BTEvent {
    private class DeepVal
    {
        public DeepVal(int npp, double val)
        {
            Npp = npp; Val = val;
        }

        public int Npp;
        public double Val;
    }
    private class ThreadDeep //Глубина резьбы в зависимости от шага
    {
        public String Name;
        public double FullDeep;
        public ArrayList<DeepVal> Tasks = new ArrayList<>();
    }
    private class StandThread
    {
        public String Name;
        public double DVal;
        public double HoleVal;
        public ThreadDeep Deep;
    }

    ArrayList<ItemModel> states = new ArrayList<>();
    RecyclerView recyclerView;
    Context context;
    MiniLathe display;

    ArrayList<StandThread> Threads = new ArrayList<>();
    ArrayList<ThreadDeep> ThDeeps = new ArrayList<>();
    StandThread th = null; //Выбранная резьба

    ActivityLatheThreadBinding binding;

    BT con = null;
    double MaxDeep = 0; //Глубина максимального прохода
    double LastZ = 0;
    double LastX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lathe_thread);
        binding.optBolt.setOnClickListener(this.optClickListener);
        binding.optNut.setOnClickListener(this.optClickListener);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bundle arguments = getIntent().getExtras();
        display = (MiniLathe) getSupportFragmentManager().findFragmentById(R.id.fragmentDisplay);

        //Передача диаметра
        if(arguments!=null){ display.setD(arguments); }

        recyclerView = findViewById(R.id.recyclerView);

        con = BT.getInstance();
        con.addListener(LatheThread.this);

        context = this;

        SelThread();
        RefreshBTData();
        ResetVals();
    }

    View.OnClickListener optClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton)v;
            switch (rb.getId()) {
                case R.id.optBolt: if (th != null)  binding.txtSizes.setText("Пруток: " + (th.DVal - (th.DVal > 19? 0.12:0.14))); binding.optNut.setChecked(false);
                    break;
                case R.id.optNut: if (th != null) binding.txtSizes.setText("Сверло: " + th.HoleVal); binding.optBolt.setChecked(false);
                    break;
                default:
                    break;
            }
        }
    };

    private void LoadMetricDictionary()
    {
        AddThread("M3*0.5",3, 2.5, AddMetricDeep(0.5));
        AddThread("  M3*0.35",3, 2.65, AddMetricDeep(0.35));
        AddThread("M4*0.7",4, 3.3,  AddMetricDeep(0.70));
        AddThread("  M4*0.5",5, 3.5, AddMetricDeep(0.5));
        AddThread("M5*0.8",5, 4.2, AddMetricDeep(0.8));
        AddThread("  M5*0.5",5, 4.5, AddMetricDeep(0.5));
        AddThread("M6*1",6, 5, AddMetricDeep(1));
        AddThread("  M6*0.75",6, 5.2, AddMetricDeep(0.75));
        AddThread("  M6*0.5",6, 5.5, AddMetricDeep(0.5));
        AddThread("M7*1",7, 6, AddMetricDeep(1));
        AddThread("  M7*0.75",7, 6.2, AddMetricDeep(0.75));
        AddThread("  M7*0.5",7, 6.5, AddMetricDeep(0.5));
        AddThread("M8*1.25",8, 6.7, AddMetricDeep(1.25));
        AddThread("  M8*1",8, 7, AddMetricDeep(1));
        AddThread("  M8*0.75",8, 7.2, AddMetricDeep(0.75));
        AddThread("  M8*0.5",8, 7.5, AddMetricDeep(0.5));
        AddThread("M10*1.5",10, 8.5, AddMetricDeep(1.5));
        AddThread("  M10*1.25",10, 8.7, AddMetricDeep(1.25));
        AddThread("  M10*1",10, 9, AddMetricDeep(1));
        AddThread("  M10*0.75",10, 9.2, AddMetricDeep(0.75));
        AddThread("  M10*0.5",10, 9.5, AddMetricDeep(0.5));
        AddThread("M11*1.5",11, 9.5, AddMetricDeep(1.5));
        AddThread("  M11*1",11, 10, AddMetricDeep(1));
        AddThread("  M11*0.75",11, 10.2, AddMetricDeep(0.75));
        AddThread("  M11*0.5",11, 10.5, AddMetricDeep(0.5));
        AddThread("M12*1.75",12, 10.2, AddMetricDeep(1.75));
        AddThread("  M12*1.5",12, 10.5, AddMetricDeep(1.5));
        AddThread("  M12*1.25",12, 10.7, AddMetricDeep(1.25));
        AddThread("  M12*1",12, 11, AddMetricDeep(1));
        AddThread("  M12*0.75",12, 11.2, AddMetricDeep(0.75));
        AddThread("  M12*0.5",12, 11.5, AddMetricDeep(0.5));
        AddThread("M14*2",14, 12, AddMetricDeep(2));
        AddThread("  M14*1.5",14, 12.5, AddMetricDeep(1.5));
        AddThread("  M14*1.25",14, 12.6, AddMetricDeep(1.25));
        AddThread("  M14*1",14, 13, AddMetricDeep(1));
        AddThread("  M14*0.75",14, 13.2, AddMetricDeep(0.75));
        AddThread("  M14*0.5",14, 13.5, AddMetricDeep(0.5));
        AddThread("M16*2",16, 14, AddMetricDeep(2));
        AddThread("  M16*1.5",16, 14.5, AddMetricDeep(1.5));
        AddThread("  M16*1",16, 15, AddMetricDeep(1));
        AddThread("  M16*0.75",16, 15.2, AddMetricDeep(0.75));
        AddThread("  M16*0.5",16, 15.5, AddMetricDeep(0.5));
        AddThread("M18*2.5",18, 15.4, AddMetricDeep(2.5));
        AddThread("  M18*2",18, 16, AddMetricDeep(2));
        AddThread("  M18*1.5",18, 16.5, AddMetricDeep(1.5));
        AddThread("  M18*1",18, 17, AddMetricDeep(1));
        AddThread("  M18*0.75",18, 17.2, AddMetricDeep(0.75));
        AddThread("  M18*0.5",18, 17.5, AddMetricDeep(0.5));
        AddThread("M20*2.5",20, 17.4, AddMetricDeep(2.5));
        AddThread("  M20*2",20, 18, AddMetricDeep(2));
        AddThread("  M20*1.5",20, 18.5, AddMetricDeep(1.5));
        AddThread("  M20*1",20, 19, AddMetricDeep(1));
        AddThread("  M20*0.75",20, 19.2, AddMetricDeep(0.75));
        AddThread("  M20*0.5",20, 19.5, AddMetricDeep(0.5));
        AddThread("M22*2.5",22, 19.4, AddMetricDeep(2.5));
        AddThread("  M22*2",22, 20, AddMetricDeep(2));
        AddThread("  M22*1.5",22, 20.5, AddMetricDeep(1.5));
        AddThread("  M22*1",22, 21, AddMetricDeep(1));
        AddThread("  M22*0.75",22, 21.2, AddMetricDeep(0.75));
        AddThread("  M22*0.5",22, 21.5, AddMetricDeep(0.5));
        AddThread("M24*3",24, 20.9, AddMetricDeep(3));
        AddThread("  M24*2",24, 22, AddMetricDeep(2));
        AddThread("  M24*1.5",24, 22.5, AddMetricDeep(1.5));
        AddThread("  M24*1",24, 23, AddMetricDeep(1));
        AddThread("  M24*0.75",24, 23.2, AddMetricDeep(0.75));
        AddThread("M27*3",27, 23.9, AddMetricDeep(3));
        AddThread("  M27*2",27, 25, AddMetricDeep(2));
        AddThread("  M27*1.5",27, 25.5, AddMetricDeep(1.5));
        AddThread("  M27*1",27, 26, AddMetricDeep(1));
        AddThread("  M27*0.75",27, 26.2, AddMetricDeep(0.75));
        AddThread("M30*3.5",30, 26.4, AddMetricDeep(3.5));
        AddThread("  M30*3",30, 26.9, AddMetricDeep(3));
        AddThread("  M30*2",30, 28, AddMetricDeep(2));
        AddThread("  M30*1.5",30, 28.5, AddMetricDeep(1.5));
        AddThread("  M30*1",30, 29, AddMetricDeep(1));
        AddThread("  M30*0.75",30, 29.2, AddMetricDeep(0.75));
        AddThread("M33*3.5",33, 29.4, AddMetricDeep(3.5));
        AddThread("  M33*3",33, 29.9, AddMetricDeep(3));
        AddThread("  M33*2",33, 31, AddMetricDeep(2));
        AddThread("  M33*1.5",33, 31.5, AddMetricDeep(1.5));
        AddThread("  M33*1",33, 32, AddMetricDeep(1));
        AddThread("  M33*0.75",33, 32.2, AddMetricDeep(0.75));
        AddThread("M36*4",36, 31.9, AddMetricDeep(4));
        AddThread("  M36*3",36, 32.9, AddMetricDeep(3));
        AddThread("  M36*2",36, 34, AddMetricDeep(2));
        AddThread("  M36*1.5",36, 34.5, AddMetricDeep(1.5));
        AddThread("  M36*1",36, 35, AddMetricDeep(1));
    }

    private void LoadInchDictionary()
    {
        AddThread("N6–32", 3.50, 2.85, AddInchDeep(32));
        AddThread("N8–32", 4.16, 3.50, AddInchDeep(32));
        AddInchDeep(28);
        AddThread("N10–24", 4.82, 4.00, AddInchDeep(24));
        AddThread("N12–24", 5.48, 4.65, AddInchDeep(24));
        AddThread("1/4″–20", 6.35, 5.35, AddInchDeep(20));
        AddThread("5/16″–18", 7.93, 6.80, AddInchDeep(18));
        AddThread("3/8″–16", 9.52, 8.25, AddInchDeep(16));
        AddThread("7/16″–14", 11.11, 9.65, AddInchDeep(14));
        AddThread("1/2″–13", 12.70, 11.15, AddInchDeep(13));
        AddThread("9/16″–12", 14.28, 12.60, AddInchDeep(12));
        AddThread("5/8″–11", 15.87, 14.05, AddInchDeep(11));
        AddThread("3/4″–10", 19.05, 17.00, AddInchDeep(10));
        AddThread("7/8″–9", 22.22, 20.00, AddInchDeep(9));
        AddThread("1″–8", 25.40, 22.25, AddInchDeep(8));
        AddThread("1.1/8″–7", 28.57, 25.65, AddInchDeep(7));
        AddThread("1.1/4″–7", 31.75, 28.85, AddInchDeep(7));
        AddThread("1.3/8″–6", 34.92, 31.55, AddInchDeep(6));
        AddThread("1.1/2″–6", 38.1, 34.70, AddInchDeep(6));
        AddThread("1.3/4″–5", 44.45, 40.40, AddInchDeep(5));
    }

    @org.jetbrains.annotations.NotNull
    private ThreadDeep AddMetricDeep(double Deep)
    {
        for (ThreadDeep t:ThDeeps) {
            if (t.Name.equals(String.valueOf(Deep)))
                return t;
        }

        ThreadDeep td = new ThreadDeep();
        td.Name = String.valueOf(Deep);
        switch((int)(Deep*100))
        {
            case 35:
                td.FullDeep = 0.42;
                td.Tasks.add(new DeepVal(1, 0.20));
                td.Tasks.add(new DeepVal(2, 0.15));
                td.Tasks.add(new DeepVal(3, 0.07));
                break;
            case 50:
                td.FullDeep = 0.58;
                td.Tasks.add(new DeepVal(1, 0.18));
                td.Tasks.add(new DeepVal(2, 0.14));
                td.Tasks.add(new DeepVal(3, 0.14));
                td.Tasks.add(new DeepVal(4, 0.12));
                break;
            case 70:
                td.FullDeep = 0.80;
                td.Tasks.add(new DeepVal(1, 0.27));
                td.Tasks.add(new DeepVal(2, 0.23));
                td.Tasks.add(new DeepVal(3, 0.18));
                td.Tasks.add(new DeepVal(4, 0.12));
                break;
            case 75:
                td.FullDeep = 0.86;
                td.Tasks.add(new DeepVal(1, 0.30));
                td.Tasks.add(new DeepVal(2, 0.26));
                td.Tasks.add(new DeepVal(3, 0.18));
                td.Tasks.add(new DeepVal(4, 0.12));
                break;
            case 80:
                td.FullDeep = 1.00;
                td.Tasks.add(new DeepVal(1, 0.30));
                td.Tasks.add(new DeepVal(2, 0.26));
                td.Tasks.add(new DeepVal(3, 0.18));
                td.Tasks.add(new DeepVal(4, 0.14));
                td.Tasks.add(new DeepVal(5, 0.12));
                break;
            case 100:
                td.FullDeep = 116;
                td.Tasks.add(new DeepVal(1, 0.34));
                td.Tasks.add(new DeepVal(2, 0.30));
                td.Tasks.add(new DeepVal(3, 0.22));
                td.Tasks.add(new DeepVal(4, 0.18));
                td.Tasks.add(new DeepVal(5, 0.12));
                break;
            case 125:
                td.FullDeep = 1.44;
                td.Tasks.add(new DeepVal(1, 0.36));
                td.Tasks.add(new DeepVal(2, 0.32));
                td.Tasks.add(new DeepVal(3, 0.24));
                td.Tasks.add(new DeepVal(4, 0.22));
                td.Tasks.add(new DeepVal(5, 0.18));
                td.Tasks.add(new DeepVal(6, 0.12));
                break;
            case 150:
                td.FullDeep = 1.74;
                td.Tasks.add(new DeepVal(1, 0.42));
                td.Tasks.add(new DeepVal(2, 0.40));
                td.Tasks.add(new DeepVal(3, 0.32));
                td.Tasks.add(new DeepVal(4, 0.26));
                td.Tasks.add(new DeepVal(5, 0.22));
                td.Tasks.add(new DeepVal(6, 0.12));
                break;
            case 175:
                td.FullDeep = 2.02;
                td.Tasks.add(new DeepVal(1, 0.42));
                td.Tasks.add(new DeepVal(2, 0.40));
                td.Tasks.add(new DeepVal(3, 0.30));
                td.Tasks.add(new DeepVal(4, 0.24));
                td.Tasks.add(new DeepVal(5, 0.20));
                td.Tasks.add(new DeepVal(6, 0.18));
                td.Tasks.add(new DeepVal(7, 0.16));
                td.Tasks.add(new DeepVal(8, 0.12));
            break;
            case 200:
                td.FullDeep = 2.30;
                td.Tasks.add(new DeepVal(1, 0.48));
                td.Tasks.add(new DeepVal(2, 0.44));
                td.Tasks.add(new DeepVal(3, 0.36));
                td.Tasks.add(new DeepVal(4, 0.28));
                td.Tasks.add(new DeepVal(5, 0.24));
                td.Tasks.add(new DeepVal(6, 0.20));
                td.Tasks.add(new DeepVal(7, 0.18));
                td.Tasks.add(new DeepVal(8, 0.12));
                break;
            case 250:
                td.FullDeep = 2.88;
                td.Tasks.add(new DeepVal(1, 0.50));
                td.Tasks.add(new DeepVal(2, 0.48));
                td.Tasks.add(new DeepVal(3, 0.42));
                td.Tasks.add(new DeepVal(4, 0.30));
                td.Tasks.add(new DeepVal(5, 0.26));
                td.Tasks.add(new DeepVal(6, 0.24));
                td.Tasks.add(new DeepVal(7, 0.20));
                td.Tasks.add(new DeepVal(8, 0.18));
                td.Tasks.add(new DeepVal(9, 0.18));
                td.Tasks.add(new DeepVal(10, 0.12));
                break;
            case 300:
                td.FullDeep = 3.46;
                td.Tasks.add(new DeepVal(1, 0.52));
                td.Tasks.add(new DeepVal(2, 0.50));
                td.Tasks.add(new DeepVal(3, 0.44));
                td.Tasks.add(new DeepVal(4, 0.34));
                td.Tasks.add(new DeepVal(5, 0.28));
                td.Tasks.add(new DeepVal(6, 0.26));
                td.Tasks.add(new DeepVal(7, 0.24));
                td.Tasks.add(new DeepVal(8, 0.22));
                td.Tasks.add(new DeepVal(9, 0.20));
                td.Tasks.add(new DeepVal(10, 0.18));
                td.Tasks.add(new DeepVal(11, 0.18));
                td.Tasks.add(new DeepVal(12, 0.12));
            break;
            case 350:
                td.FullDeep = 4.04;
                td.Tasks.add(new DeepVal(1, 0.64));
                td.Tasks.add(new DeepVal(2, 0.60));
                td.Tasks.add(new DeepVal(3, 0.46));
                td.Tasks.add(new DeepVal(4, 0.38));
                td.Tasks.add(new DeepVal(5, 0.34));
                td.Tasks.add(new DeepVal(6, 0.30));
                td.Tasks.add(new DeepVal(7, 0.28));
                td.Tasks.add(new DeepVal(8, 0.26));
                td.Tasks.add(new DeepVal(9, 0.24));
                td.Tasks.add(new DeepVal(10, 0.22));
                td.Tasks.add(new DeepVal(11, 0.20));
                td.Tasks.add(new DeepVal(12, 0.12));
                break;
            case 400:
                td.FullDeep = 4.62;
                td.Tasks.add(new DeepVal(1, 0.66));
                td.Tasks.add(new DeepVal(2, 0.62));
                td.Tasks.add(new DeepVal(3, 0.48));
                td.Tasks.add(new DeepVal(4, 0.44));
                td.Tasks.add(new DeepVal(5, 0.36));
                td.Tasks.add(new DeepVal(6, 0.30));
                td.Tasks.add(new DeepVal(7, 0.28));
                td.Tasks.add(new DeepVal(8, 0.26));
                td.Tasks.add(new DeepVal(9, 0.24));
                td.Tasks.add(new DeepVal(10, 0.24));
                td.Tasks.add(new DeepVal(11, 0.22));
                td.Tasks.add(new DeepVal(12, 0.20));
                td.Tasks.add(new DeepVal(12, 0.20));
                td.Tasks.add(new DeepVal(12, 0.12));
                break;
        }
        ThDeeps.add(td);
        return td;
    }

    @org.jetbrains.annotations.NotNull
    private ThreadDeep AddInchDeep (int Deep) {
        for (ThreadDeep t : ThDeeps) {
            if (t.Name.equals(String.valueOf(Deep)))
                return t;
        }

        ThreadDeep td = new ThreadDeep();
        td.Name = String.valueOf(Deep);
        switch (Deep) {
            case 32:
                td.FullDeep = 0.92;
                td.Tasks.add(new DeepVal(1, 0.32));
                td.Tasks.add(new DeepVal(2, 0.28));
                td.Tasks.add(new DeepVal(3, 0.20));
                td.Tasks.add(new DeepVal(4, 0.12));
                break;
            case 28:
                td.FullDeep = 1.04;
                td.Tasks.add(new DeepVal(1, 0.32));
                td.Tasks.add(new DeepVal(2, 0.26));
                td.Tasks.add(new DeepVal(3, 0.18));
                td.Tasks.add(new DeepVal(4, 0.16));
                td.Tasks.add(new DeepVal(5, 0.12));
                break;
            case 24:
                td.FullDeep = 1.22;
                td.Tasks.add(new DeepVal(1, 0.34));
                td.Tasks.add(new DeepVal(2, 0.30));
                td.Tasks.add(new DeepVal(3, 0.26));
                td.Tasks.add(new DeepVal(4, 0.20));
                td.Tasks.add(new DeepVal(5, 0.12));
                break;
            case 20:
                td.FullDeep = 1.46;
                td.Tasks.add(new DeepVal(1, 0.36));
                td.Tasks.add(new DeepVal(2, 0.30));
                td.Tasks.add(new DeepVal(3, 0.26));
                td.Tasks.add(new DeepVal(4, 0.22));
                td.Tasks.add(new DeepVal(5, 0.20));
                td.Tasks.add(new DeepVal(6, 0.12));
                break;
            case 18:
                td.FullDeep = 1.62;
                td.Tasks.add(new DeepVal(1, 0.40));
                td.Tasks.add(new DeepVal(2, 0.36));
                td.Tasks.add(new DeepVal(3, 0.28));
                td.Tasks.add(new DeepVal(4, 0.24));
                td.Tasks.add(new DeepVal(5, 0.22));
                td.Tasks.add(new DeepVal(6, 0.12));
                break;
            case 16:
                td.FullDeep = 1.84;
                td.Tasks.add(new DeepVal(1, 0.40));
                td.Tasks.add(new DeepVal(2, 0.36));
                td.Tasks.add(new DeepVal(3, 0.30));
                td.Tasks.add(new DeepVal(4, 0.24));
                td.Tasks.add(new DeepVal(5, 0.22));
                td.Tasks.add(new DeepVal(6, 0.20));
                td.Tasks.add(new DeepVal(7, 0.12));
                break;
            case 14:
                td.FullDeep = 2.10;
                td.Tasks.add(new DeepVal(1, 0.42));
                td.Tasks.add(new DeepVal(2, 0.36));
                td.Tasks.add(new DeepVal(3, 0.30));
                td.Tasks.add(new DeepVal(4, 0.26));
                td.Tasks.add(new DeepVal(5, 0.22));
                td.Tasks.add(new DeepVal(6, 0.22));
                td.Tasks.add(new DeepVal(7, 0.20));
                td.Tasks.add(new DeepVal(8, 0.12));
                break;
            case 13:
                td.FullDeep = 2.26;
                td.Tasks.add(new DeepVal(1, 0.44));
                td.Tasks.add(new DeepVal(2, 0.38));
                td.Tasks.add(new DeepVal(3, 0.32));
                td.Tasks.add(new DeepVal(4, 0.28));
                td.Tasks.add(new DeepVal(5, 0.26));
                td.Tasks.add(new DeepVal(6, 0.24));
                td.Tasks.add(new DeepVal(7, 0.22));
                td.Tasks.add(new DeepVal(8, 0.12));
                break;
            case 12:
                td.FullDeep = 1.44;
                td.Tasks.add(new DeepVal(1, 0.48));
                td.Tasks.add(new DeepVal(2, 0.44));
                td.Tasks.add(new DeepVal(3, 0.36));
                td.Tasks.add(new DeepVal(4, 0.32));
                td.Tasks.add(new DeepVal(5, 0.26));
                td.Tasks.add(new DeepVal(6, 0.24));
                td.Tasks.add(new DeepVal(7, 0.22));
                td.Tasks.add(new DeepVal(8, 0.12));
                break;
            case 11:
                td.FullDeep = 2.66;
                td.Tasks.add(new DeepVal(1, 0.48));
                td.Tasks.add(new DeepVal(2, 0.44));
                td.Tasks.add(new DeepVal(3, 0.40));
                td.Tasks.add(new DeepVal(4, 0.30));
                td.Tasks.add(new DeepVal(5, 0.24));
                td.Tasks.add(new DeepVal(6, 0.24));
                td.Tasks.add(new DeepVal(7, 0.22));
                td.Tasks.add(new DeepVal(8, 0.22));
                td.Tasks.add(new DeepVal(9, 0.12));
                break;
            case 10:
                td.FullDeep = 2.94;
                td.Tasks.add(new DeepVal(1, 0.50));
                td.Tasks.add(new DeepVal(2, 0.44));
                td.Tasks.add(new DeepVal(3, 0.42));
                td.Tasks.add(new DeepVal(4, 0.28));
                td.Tasks.add(new DeepVal(5, 0.26));
                td.Tasks.add(new DeepVal(6, 0.24));
                td.Tasks.add(new DeepVal(7, 0.24));
                td.Tasks.add(new DeepVal(8, 0.22));
                td.Tasks.add(new DeepVal(9, 0.22));
                td.Tasks.add(new DeepVal(10, 0.12));
                break;
            case 9:
                td.FullDeep = 3.26;
                td.Tasks.add(new DeepVal(1, 0.62));
                td.Tasks.add(new DeepVal(2, 0.46));
                td.Tasks.add(new DeepVal(3, 0.42));
                td.Tasks.add(new DeepVal(4, 0.34));
                td.Tasks.add(new DeepVal(5, 0.30));
                td.Tasks.add(new DeepVal(6, 0.28));
                td.Tasks.add(new DeepVal(7, 0.26));
                td.Tasks.add(new DeepVal(8, 0.24));
                td.Tasks.add(new DeepVal(9, 0.22));
                td.Tasks.add(new DeepVal(10, 0.12));
                break;
            case 8:
                td.FullDeep = 3.66;
                td.Tasks.add(new DeepVal(1, 0.62));
                td.Tasks.add(new DeepVal(2, 0.52));
                td.Tasks.add(new DeepVal(3, 0.42));
                td.Tasks.add(new DeepVal(4, 0.36));
                td.Tasks.add(new DeepVal(5, 0.32));
                td.Tasks.add(new DeepVal(6, 0.30));
                td.Tasks.add(new DeepVal(7, 0.28));
                td.Tasks.add(new DeepVal(8, 0.26));
                td.Tasks.add(new DeepVal(9, 0.24));
                td.Tasks.add(new DeepVal(10, 0.22));
                td.Tasks.add(new DeepVal(11, 0.12));
                break;
            case 7:
                td.FullDeep = 4.18;
                td.Tasks.add(new DeepVal(1, 0.72));
                td.Tasks.add(new DeepVal(2, 0.60));
                td.Tasks.add(new DeepVal(3, 0.48));
                td.Tasks.add(new DeepVal(4, 0.42));
                td.Tasks.add(new DeepVal(5, 0.36));
                td.Tasks.add(new DeepVal(6, 0.34));
                td.Tasks.add(new DeepVal(7, 0.32));
                td.Tasks.add(new DeepVal(8, 0.30));
                td.Tasks.add(new DeepVal(9, 0.28));
                td.Tasks.add(new DeepVal(10, 0.24));
                td.Tasks.add(new DeepVal(11, 0.12));
                break;
            case 6:
                td.FullDeep = 4.88;
                td.Tasks.add(new DeepVal(1, 0.80));
                td.Tasks.add(new DeepVal(2, 0.66));
                td.Tasks.add(new DeepVal(3, 0.50));
                td.Tasks.add(new DeepVal(4, 0.46));
                td.Tasks.add(new DeepVal(5, 0.38));
                td.Tasks.add(new DeepVal(6, 0.34));
                td.Tasks.add(new DeepVal(7, 0.32));
                td.Tasks.add(new DeepVal(8, 0.30));
                td.Tasks.add(new DeepVal(9, 0.28));
                td.Tasks.add(new DeepVal(10, 0.26));
                td.Tasks.add(new DeepVal(11, 0.24));
                td.Tasks.add(new DeepVal(12, 0.22));
                td.Tasks.add(new DeepVal(13, 0.12));
                break;
            case 5:
                td.FullDeep = 5.86;
                td.Tasks.add(new DeepVal(1, 0.82));
                td.Tasks.add(new DeepVal(2, 0.70));
                td.Tasks.add(new DeepVal(3, 0.62));
                td.Tasks.add(new DeepVal(4, 0.52));
                td.Tasks.add(new DeepVal(5, 0.46));
                td.Tasks.add(new DeepVal(6, 0.42));
                td.Tasks.add(new DeepVal(7, 0.40));
                td.Tasks.add(new DeepVal(8, 0.38));
                td.Tasks.add(new DeepVal(9, 0.34));
                td.Tasks.add(new DeepVal(10, 0.30));
                td.Tasks.add(new DeepVal(11, 0.28));
                td.Tasks.add(new DeepVal(12, 0.26));
                td.Tasks.add(new DeepVal(13, 0.24));
                td.Tasks.add(new DeepVal(14, 0.12));
                break;
        }
        ThDeeps.add(td);
        return td;
    }

    private void AddThread(String Name, double DVal, double HoleVal, ThreadDeep Deep)
    {
        StandThread t = new StandThread();
        t.Name = Name;
        t.DVal = DVal;
        t.HoleVal = HoleVal;
        t.Deep = Deep;

        Threads.add(t);
    }

    @Override
    public void RefreshBTData() {
        boolean Focused = false;
        double X = 0;
        try {
            X = display.getX() * (binding.optBolt.isChecked()?1:-1); //Если режем гайку, то Х считаем наоборот.

            for (int i = 0; i < states.size(); i++) {

                ItemModel m = states.get(i);

                if (Math.abs(display.getZ() - LastZ) > 1) //Значит, есть смещение.
                {
                    MaxDeep = Math.max(MaxDeep, (X));

                    if (m.getB() <= (MaxDeep-0.03))  //3 сотки точности ведь достаточно для резьбы? )))
                        m.setCheck(true);
                    recyclerView.getAdapter().notifyItemChanged(i);
                }

                if (!Focused && m.getB() >= MaxDeep) {
                    Utils.SetRWPosition(recyclerView, i);
                    m.setFoud(true);
                    Focused = true;
                }
                else
                    m.setFoud(false);

            }

            if (Math.abs(display.getZ() - LastZ) > 1)
                LastZ = display.getZ();

            binding.txtMaxDeep.setText("Нарезано: " + ValToPrint(MaxDeep));
            LastX = display.getX();
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void ResetVals()
    {
        MaxDeep = 0;
        LastZ = con.getValZ();
        LastX = con.getValX();
    }

    private void SelThread()
    {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(LatheThread.this);
        builderSingle.setTitle("Выберите тип резьбы:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(LatheThread.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Метрическая стандартная");
        arrayAdapter.add("Метрическая своя");
        arrayAdapter.add("Дюймовая UNC");
        arrayAdapter.add("Дюймовая своя");


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
                switch (strName)
                {
                    case "Метрическая стандартная":
                        LoadMetricDictionary();
                        SelStandartThread();
                        break;
                    case "Метрическая своя":
                        LoadMetricDictionary();
                        SelStep();
                        break;
                    case "Дюймовая UNC":
                        LoadInchDictionary();
                        SelStandartThread();
                        break;
                    case "Дюймовая своя":
                        LoadInchDictionary();
                        SelStep();
                        break;
                }
            }
        });
        builderSingle.show();
    }

    private void SelStandartThread() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(LatheThread.this);
        builderSingle.setTitle("Выберите резьбу:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(LatheThread.this, android.R.layout.select_dialog_singlechoice);
        for (StandThread thread : Threads) {
            arrayAdapter.add(thread.Name);
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
                binding.txtThreadName.setText(strName.trim());

                for (StandThread thread : Threads) {
                    if (thread.Name.equals(strName))                    {
                        th = thread; break;
                    }
                }

                binding.txtSizes.setText("Пруток: " + (th.DVal - (th.DVal > 19? 0.12:0.14)));
                binding.txtThreadDeep.setText("Глубина: " + th.Deep.FullDeep);

                ResetVals();

                int bTotal = 0;
                for (DeepVal val : th.Deep.Tasks) {
                    if (val.Val > 0){
                        bTotal += val.Val*100;
                        states.add(new ItemModel(val.Npp,"ΔX" ,"∑X", val.Val, (float)bTotal/100));
                        //adapter.add(val.Npp + ") " + String.format("%.2f", val.Val) + "   ["+String.format("%.2f",((float)bTotal/100))+"]");
                    }
                }

                ItemAdapter adapter = new ItemAdapter(context, states);
                RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
                recyclerView.setAdapter(adapter);
            }
        });
        builderSingle.show();
    }

    private void SelStep() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(LatheThread.this);
        builderSingle.setTitle("Выберите шаг:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(LatheThread.this, android.R.layout.select_dialog_singlechoice);
        for (ThreadDeep thread : ThDeeps) {
            arrayAdapter.add(thread.Name);
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

                binding.txtThreadName.setText("XX*" + strName.trim());
                ResetVals();

                ThreadDeep td = null;
                for (ThreadDeep thread : ThDeeps) {
                    if (thread.Name.equals(strName))                    {
                        td = thread; break;
                    }
                }

                binding.txtSizes.setVisibility(View.INVISIBLE);
                binding.txtThreadDeep.setVisibility(View.INVISIBLE);

                int bTotal = 0;
                for (DeepVal val : td.Tasks) {
                    if (val.Val > 0){
                        bTotal += val.Val*100;
                        states.add(new ItemModel(val.Npp,"ΔX" ,"∑X", val.Val, (float)bTotal/100));
                        //adapter.add(val.Npp + ") " + String.format("%.2f", val.Val) + "   ["+String.format("%.2f",((float)bTotal/100))+"]");
                    }
                }

                ItemAdapter adapter = new ItemAdapter(context, states);
                RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
                recyclerView.setAdapter(adapter);
            }
        });
        builderSingle.show();
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Выйти?")
                .setMessage("Вы действительно хотите выйти?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> LatheThread.super.onBackPressed()).create().show();
    }
}
