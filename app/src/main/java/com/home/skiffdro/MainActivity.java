package com.home.skiffdro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.home.skiffdro.common.BT;
import com.home.skiffdro.common.BTEvent;
import com.home.skiffdro.fragments.LatheMain;
import com.home.skiffdro.fragments.MillingMain;
import com.home.skiffdro.lathe.LatheAngleMeter;
import com.home.skiffdro.lathe.LatheBoll;
import com.home.skiffdro.lathe.LathePulley;
import com.home.skiffdro.lathe.LatheThread;
import com.home.skiffdro.milling.MillingRoundDrill;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements BTEvent {
    BT con = null;
    String DeviceMAC = "";
    String DeviceName;

    ListView TasksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //А блютус то есть? А включен?
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null) {
            if (!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 0); //REQUEST_ENABLE_BT
            }
        } else {
            finish();
            return;
        }

        if (savedInstanceState != null && savedInstanceState.containsKey("DeviceMAC")) {
            DeviceMAC = savedInstanceState.getString("DeviceMAC");
            con = BT.getInstance(DeviceMAC);
        }
        else
            SelBTDevice();


        TasksList = (ListView)findViewById(R.id.TasksList);

        TasksList.setOnItemClickListener((parent, itemClicked, position, id) -> {
            TextView textView = (TextView) itemClicked;
            String strText = textView.getText().toString(); // получаем текст нажатого элемента

            Intent intent = null;
            switch(strText)
            {
                case "Нарезание резьбы":
                    intent = new Intent(MainActivity.this, LatheThread.class);
                    break;
                case "Канавки шкивов":
                    intent = new Intent(MainActivity.this, LathePulley.class);
                    break;
                case "Угломер":
                    intent = new Intent(MainActivity.this, LatheAngleMeter.class);
                    break;
                case "Шарик":
                    LatheMain LatFragment = (LatheMain) getSupportFragmentManager().findFragmentById(R.id.fragmentMain);
                    if (LatFragment.DSetted())
                    {
                        intent = new Intent(MainActivity.this, LatheBoll.class);
                        intent.putExtra("ScalesDfixX", LatFragment.getScalesDfixX());
                        intent.putExtra("ScalesDsetX", LatFragment.getScalesDsetX());
                    }
                    else
                        Toast.makeText(MainActivity.this, "Не привязан диаметр!", Toast.LENGTH_SHORT).show();
                    break;
                case "Сверление по окружности":
                    MillingMain MillFragment = (MillingMain) getSupportFragmentManager().findFragmentById(R.id.fragmentMain);
                    if (MillFragment.CenterXFound() && MillFragment.CenterYFound())
                    {
                        intent = new Intent(MainActivity.this, MillingRoundDrill.class);
                        intent.putExtra("CenterX", MillFragment.CenterX());
                        intent.putExtra("CenterY", MillFragment.CenterY());
                    }
                    else
                        Toast.makeText(MainActivity.this, "Не найден центр!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + strText);
            }
            if (intent != null)startActivity(intent);
        });
    }

    @Override
    protected void onStart() { // Запрос на включение Bluetooth
        super.onStart();
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 1);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        saveInstanceState.putString("DeviceMAC", DeviceMAC);
        super.onSaveInstanceState(saveInstanceState);
    }

    @Override
    public void RefreshBTData() {
        runOnUiThread(() -> {
            if (con.getIsConnected())
                setTitle("Подключено: " + con.getDeviceType() + " " + DeviceName);
            else
                setTitle("Подключение к " + DeviceName);

            if (TasksList.getAdapter() == null)
            {
                if (con.getDeviceType().equals("Токарный")) PrepareAsLathe();
                if (con.getDeviceType().equals("Фрезерный")) PrepareAsMilling();
            }
    });
    }

    private  void PrepareAsLathe()
    {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentMain, LatheMain.class, null)
                .commit();

        ArrayList<String> Tasks = new ArrayList<>();
        Tasks.add("Нарезание резьбы");
        Tasks.add("Канавки шкивов");
        //Tasks.add("Угломер");
        Tasks.add("Шарик");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Tasks)
        {
            public View getView(int position, View convertView, ViewGroup parent){
                TextView item = (TextView) super.getView(position,convertView,parent);
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
                return item;
            }
        };
        TasksList.setAdapter(adapter);
    }

    private void PrepareAsMilling()
    {
        getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentMain, MillingMain.class, null)
                    .commit();

        ArrayList<String> Tasks = new ArrayList<>();
        Tasks.add("Сверление по окружности");
        //Tasks.add("Скругление углов");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Tasks)
        {
            public View getView(int position, View convertView, ViewGroup parent){
                TextView item = (TextView) super.getView(position,convertView,parent);
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
                return item;
            }
        };
        TasksList.setAdapter(adapter);
    }

    private void SelBTDevice() { // Создание списка сопряжённых Bluetooth-устройств

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setTitle("Выберите устройство:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for (BluetoothDevice device : pairedDevices) { // Добавляем сопряжённые устройства - Имя + MAC-адресс
            arrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }

        builderSingle.setNegativeButton("Отмена", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            String strName = arrayAdapter.getItem(which);
            DeviceName = strName.substring(0,strName.length() - 18);
            Toast.makeText(MainActivity.this, "Подключаюсь к " + DeviceName, Toast.LENGTH_SHORT).show();
            DeviceMAC = strName.substring(strName.length() - 17); // Вычленяем MAC-адрес
            con = BT.getInstance(DeviceMAC); // new BT();
            con.addListener(MainActivity.this);
        });
        builderSingle.show();
    }


    @Override
    protected void onDestroy() { // Закрытие приложения
        super.onDestroy();
        con.cancel();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { // Если разрешили включить Bluetooth, тогда void setup()
            if (resultCode == Activity.RESULT_OK) {
                SelBTDevice();
            } else { // Если не разрешили, тогда закрываем приложение
                Toast.makeText(this, "BlueTooth не включён", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
