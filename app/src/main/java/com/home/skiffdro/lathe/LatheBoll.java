package com.home.skiffdro.lathe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.home.skiffdro.common.BT;
import com.home.skiffdro.common.BTEvent;
import com.home.skiffdro.R;

public class LatheBoll extends AppCompatActivity implements BTEvent, TextWatcher {

    BT con = null;

    double ScalesDsetX = 0; //Установленный размер диаметра
    double ScasesDfixX = 0; //АБСОЛЮТНОЕ значение линейки для установленного диаметра

    TextView txtSteps, txtD;


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
        getSupportActionBar().hide();

        con = BT.getInstance();
        con.addListener(LatheBoll.this);


//        txtRWidth = (TextView) findViewById(R.id.txtRWidth);
//        txtSteps = (TextView) findViewById(R.id.txtSteps);
//        txtD = (TextView) findViewById(R.id.txtD);
//
//        lblX = (TextView) findViewById(R.id.lblX);
//        lblY = (TextView) findViewById(R.id.lblY);
//        cmdSetX = (Button) findViewById(R.id.cmdSetX);
//        cmdResetY = (Button) findViewById(R.id.cmdResetY);
//        BollSteps = (ListView) findViewById(R.id.txtSteps);
//
//        cmdSetX.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                showInputDialog();
//            }
//        });
//        cmdResetY.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                ScalesOffsetY = ScalesValY;
//            }
//        });

        txtSteps.addTextChangedListener(LatheBoll.this);
        //txtRWidth.addTextChangedListener(LatheBoll.this);
        txtD.addTextChangedListener(LatheBoll.this);

    }

    @Override
    public void RefreshBTData() {
//        ScalesValX = con.getValX();
//        ScalesValY = con.getValY();
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                double D = 0;
//                if (ScalesDsetX == 0)
//                    lblX.setText("???");
//                else {
//                    D = ScalesDsetX - (ScasesDfixX - ScalesValX) * -1;
//                    lblX.setText(ValToPrint(D));
//                }
//
//                lblY.setText(ValToPrint((ScalesValY - ScalesOffsetY)));
//
//                if (BollSteps != null && BollSteps.getCount() > 0) {
//                    for (int i = 0; i < BollSteps.getCount(); i++) {
//
//                        TextView vv = (TextView) BollSteps.getAdapter().getView(i, null, null);
//                        String s = vv.getText().toString();
//                        double sy = Double.parseDouble(s.substring(s.indexOf("\t") + 1).replace(",", ".").substring(0,3));
//                        double sx = Double.parseDouble(s.substring(s.indexOf("\t\t") + 2).replace(",", "."));
//
//                        if (Math.abs(sy - (ScalesValY - ScalesOffsetY)) <= 0.05) {
//
//                            int h1 = BollSteps.getHeight();
//                            int h2 = BollSteps.getChildAt(0).getHeight();
//
//                            BollSteps.setSelection(i);
//                            BollSteps.requestFocus();
//
//                            for (int nn = 0; nn < h1/h2; nn++) {
//                                if (nn == i - BollSteps.getFirstVisiblePosition())
//                                    BollSteps.getChildAt(nn).setBackgroundColor(Color.YELLOW);
//                                else
//                                    BollSteps.getChildAt(nn).setBackgroundColor(Color.TRANSPARENT);
//                            }
//                        }
////                        else
////                            BollSteps.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
//
////                        if (Math.abs(D-sx) <= 0.05 && Math.abs(sy - (ScalesValY - ScalesOffsetY)) <= 0.05) {
////                            BollSteps.setItemChecked(i - BollSteps.getFirstVisiblePosition(), true);
////                        }
//
//
//                    }
//                }
//            }
//        });

    }

//    private void CalcTable()
//    {
//        if (txtD.getText().length() == 0 || txtSteps.getText().length() == 0 || txtRWidth.getText().length() == 0)
//            return;
//
//        double d = Double.parseDouble(String.valueOf(txtD.getText()));
//        int Stps = Integer.parseInt(String.valueOf(txtSteps.getText()));;
//        double H = Double.parseDouble(String.valueOf(txtRWidth.getText()));;
//
//        double r = d / 2;
//        vals[] arr = new vals[Stps+2];
//
//        int N = Stps / 2;
//
//        for (int i = 0; i < N + 1; i++)
//        {
//            vals v = new vals();
//            v.X = (r / (Stps / 2))* i;
//            v.Y = Math.sqrt((r * r) - (v.X * v.X)) * 2;
//            arr[i+N] = v;
//            arr[i + N].X += r;
//        }
//
//        for (int i = 0; i < N+1; i++)
//        {
//            vals v = new vals();
//            v.X = d-arr[i + N].X;
//            v.Y = arr[i + N].Y;
//            arr[N - i] = v;
//
//            arr[i + N].X += H;
//        }
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LatheBoll.this, android.R.layout.simple_list_item_multiple_choice)
//        {
//            public View getView(int position, View convertView, ViewGroup parent){
//                TextView item = (TextView) super.getView(position,convertView,parent);
//                item.setTextColor(Color.parseColor("#FF3E80F1"));
//                item.setTypeface(item.getTypeface(), Typeface.BOLD);
//                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,26);
//                return item;
//            }
//        };
//
//        for (int i = 0; i < Stps+1; i++) {
//            if (arr[i] != null)
//                adapter.add((i + 1) + ")\t" + String.format("%.2f", arr[i].X) + "\t\t" + String.format("%.2f", arr[i].Y).replace("NaN", "0,00"));
//        }
//
//        //BollSteps.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        //BollSteps.setAdapter(adapter);
//
//
//    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
       // CalcTable();
    }
}