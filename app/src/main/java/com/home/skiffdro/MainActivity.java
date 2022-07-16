package com.home.skiffdro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.home.skiffdro.common.connections.BT;
import com.home.skiffdro.common.connections.Connection;
import com.home.skiffdro.common.connections.IConnection;
import com.home.skiffdro.common.connections.ConnectionEvent;
import com.home.skiffdro.common.adapters.CenterSmoothScroller;
import com.home.skiffdro.common.adapters.MarkAdapter;
import com.home.skiffdro.common.dialogs.MarkDialog;
import com.home.skiffdro.common.Notifier;
import com.home.skiffdro.common.Setts;
import com.home.skiffdro.common.SettsActivity;
import com.home.skiffdro.common.connections.USB;
import com.home.skiffdro.common.Utils;
import com.home.skiffdro.common.dialogs.YesNoDialog;
import com.home.skiffdro.fragments.LatheMain;
import com.home.skiffdro.fragments.MillingMain;
import com.home.skiffdro.lathe.LatheAngleMeter;
import com.home.skiffdro.lathe.LatheBoll;
import com.home.skiffdro.lathe.LathePulley;
import com.home.skiffdro.lathe.LatheThread;
import com.home.skiffdro.milling.MillingRoundDrill;
import com.home.skiffdro.models.MarkModel;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements ConnectionEvent {
    Connection con = null;

    String DeviceMAC = "";
    String DeviceName;

    boolean IsPortret = false;
    boolean IsInitialized = false;
    Fragment CurrDisplay;

    RecyclerView recyclerView;
    ArrayList<MarkModel> Marks = new ArrayList<>();
    MarkAdapter adapter;

    MediaPlayer MarkBell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Setts sets = Setts.getInstance(getApplicationContext());
        IsPortret = sets.getIsPortret();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MarkBell = MediaPlayer.create(this, R.raw.markbell);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (IsPortret)
        {
            ((LinearLayout) findViewById(R.id.llMarks)).setWeightSum(1);
            ((LinearLayout) findViewById(R.id.frLandscape)).setVisibility(View.GONE);
        }

        USB usb = USB.getInstance(this);

        if (usb.getIsConnected() && sets.getIsUseUSB())
        {
            Toast.makeText(MainActivity.this, "Найдено подключение по USB!", Toast.LENGTH_LONG).show();
            DeviceName = "USB";
            usb.addListener(this);
            con = Connection.Init();
            con.setInstance(usb);
        }
        else
        {
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
                con = Connection.Init();
                con.setInstance(BT.getInstance(DeviceMAC));
            } else
                SelBTDevice();
        }

        Button cmdAddMark = findViewById(R.id.cmdAddMarks);
        Button cmdResetMarks = findViewById(R.id.cmdResetMarks);

        //Добавление метки
        cmdAddMark.setOnClickListener(v -> new MarkDialog(new MarkDialog.DialogEvent() {
            @Override
            public void DialogOK(MarkModel m) {
                try {
                    Marks.add(m);
                    adapter.notifyDataSetChanged();
                }
                catch (Exception ex){}
            }

            @Override
            public void DialogCancel() {  }
        }, MainActivity.this, CurrDisplay ));

        //удаление всех меток
        cmdResetMarks.setOnClickListener(v -> new YesNoDialog(new YesNoDialog.DialogEvent() {
            @Override
            public void DialogYes() {
                try {Marks.clear();
                    adapter.notifyDataSetChanged();
                }
                catch (Exception ex){}
            }

            @Override
            public void DialogNo() {

            }
        }, MainActivity.this, "Удаление всех меток.","Вы уверены?" ));

        recyclerView = findViewById(R.id.MarksList);
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
        adapter = new MarkAdapter(this, Marks);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.def_menu, menu);
        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (con != null) {
            menu.clear();
            MenuInflater inflater = getMenuInflater();
            if (con.getDeviceType() == BT.DeviceType.Lathe)
                inflater.inflate(R.menu.lathe_menu, menu);
            else
                inflater.inflate(R.menu.milling_menu, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int fragmentMain = IsPortret? R.id.frPortret:R.id.frLandscape;
        String strText = item.getTitle().toString(); // получаем текст нажатого элемента

        Intent intent = null;
        switch(strText)
        {
            case "Настройки":
                intent = new Intent(MainActivity.this, SettsActivity.class);
                break;
            case "Нарезание резьбы":
                intent = new Intent(MainActivity.this, LatheThread.class);
                ((LatheMain) getSupportFragmentManager().findFragmentById(fragmentMain)).SetIntentDval(intent);
                break;
            case "Нарезка шкивов":
                intent = new Intent(MainActivity.this, LathePulley.class);
                ((LatheMain) getSupportFragmentManager().findFragmentById(fragmentMain)).SetIntentDval(intent);
                break;
            case "Угломер":
                intent = new Intent(MainActivity.this, LatheAngleMeter.class);
                break;
            case "Вытачивание шарика":
                LatheMain LatFragmentB = (LatheMain) getSupportFragmentManager().findFragmentById(fragmentMain);
                if (LatFragmentB.DSetted())
                {
                    intent = new Intent(MainActivity.this, LatheBoll.class);
                    LatFragmentB.SetIntentDval(intent);
                }
                else
                    Toast.makeText(MainActivity.this, "Не привязан диаметр!", Toast.LENGTH_SHORT).show();
                break;
            case "Сверление по окружности":
                MillingMain MillFragment = (MillingMain) getSupportFragmentManager().findFragmentById(fragmentMain);
                if (MillFragment.CenterXFound() && MillFragment.CenterYFound())
                {
                    intent = new Intent(MainActivity.this, MillingRoundDrill.class);
                    MillFragment.SetIntentCenter(intent);
                }
                else
                    Toast.makeText(MainActivity.this, "Не найден центр!", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + strText);
        }
        if (intent != null)startActivity(intent);


        return false;
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
    public void RefreshData() {
        runOnUiThread(() -> {
            if (con.getIsConnected() && con.getDeviceType() != BT.DeviceType.None ) {
                setTitle("Подключено: " + (con.getDeviceType() == BT.DeviceType.Lathe?"Токарный":"Фрезерный") + " " + DeviceName);
                //setTitle(con.getBTStatus());

                if (CurrDisplay == null) {
                    if (IsPortret)
                        CurrDisplay = getSupportFragmentManager().findFragmentById(R.id.frPortret);
                    else
                        CurrDisplay = getSupportFragmentManager().findFragmentById(R.id.frLandscape);
                }

                if (!IsInitialized)
                {
                    if (con.getDeviceType() == BT.DeviceType.Lathe) PrepareAsLathe();
                    if (con.getDeviceType() == BT.DeviceType.Milling) PrepareAsMilling();

                    findViewById(R.id.cmdAddMarks).setEnabled(true);
                    findViewById(R.id.cmdResetMarks).setEnabled(true);
                    IsInitialized = true;
                }
                else
                {
                    //Обработка меток
                    if (con.getDeviceType() == BT.DeviceType.Lathe) {
                        LatheMain lm = (LatheMain)CurrDisplay;
                        for (int i = 0; i < Marks.size(); i++) {
                            MarkModel m = Marks.get(i);
                            boolean Fnd = (!m.HaveA() || Math.abs(lm.getX() - m.getA()) <= 0.5) &&
                                    (!m.HaveC() || Math.abs(lm.getZ() - m.getC()) <= 0.5);

                            if (!m.getFound() && Fnd)
                            {
                                if (m.getNotify()) {

                                    Notifier n = new Notifier(this);
                                    String msg = "Достигнута засечка!";
                                    if (m.getName().length() > 0) msg = m.getName();
                                    n.ShowMarkAlarm(msg);
                                }
                                else
                                    MarkBell.start();
                            }

                            if (m.getFound() != Fnd) {
                                m.setFoud(Fnd);
                                recyclerView.getAdapter().notifyItemChanged(i);
                            }

                            if (Fnd) Utils.SetRWPosition(recyclerView, i);
                        }
                    } else {
                        MillingMain mm = (MillingMain)CurrDisplay;
                        for (int i = 0; i < Marks.size(); i++) {
                            MarkModel m = Marks.get(i);
                            boolean Fnd = (!m.HaveA() || Math.abs(mm.getX() - m.getA()) <= 0.5) &&
                                    (!m.HaveB() || Math.abs(mm.getY() - m.getB()) <= 0.5) &&
                                    (!m.HaveC() || Math.abs(mm.getZ() - m.getC()) <= 0.5);

                            if (!m.getFound() && Fnd)
                            {
                                if (m.getNotify()) {

                                    Notifier n = new Notifier(this);
                                    String msg = "Достигнута засечка!";
                                    if (m.getName().length() > 0) msg = m.getName();
                                    n.ShowMarkAlarm(msg);
                                }
                                else
                                    MarkBell.start();
                            }

                            if (m.getFound() != Fnd) {
                                m.setFoud(Fnd);
                                recyclerView.getAdapter().notifyItemChanged(i);
                            }

                            if (Fnd) Utils.SetRWPosition(recyclerView, i);
                        }
                    }
                }
            }
            else
                setTitle("Подключение к " + DeviceName);
    });
    }

    private  void PrepareAsLathe()
    {
        if (IsPortret)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frPortret, LatheMain.class, null)
                    .commit();
        else
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frLandscape, LatheMain.class, null)
                    .commit();
    }

    private void PrepareAsMilling()
    {
        if (IsPortret)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frPortret, MillingMain.class, null)
                    .commit();
        else
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frLandscape, MillingMain.class, null)
                    .commit();


    }

    private void SelBTDevice() { // Создание списка сопряжённых Bluetooth-устройств

        Setts s = Setts.getInstance();
        Set<String> Sel = s.getBTDevicesList();

        if (Sel != null && Sel.size() == 1){
            StartBTConnect(Sel.iterator().next());
            return;
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setTitle("Выберите устройство:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();



        for (BluetoothDevice device : pairedDevices) { // Добавляем сопряжённые устройства - Имя + MAC-адресс
            if (Sel == null || Sel.size() == 0 || Sel.contains(device.getName() + "\n" + device.getAddress()))
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }

        builderSingle.setNegativeButton("Отмена", (dialog, which) -> {
            dialog.dismiss();
//            finish();
        });

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            String strName = arrayAdapter.getItem(which);
            StartBTConnect(strName);
        });
        builderSingle.show();
    }

    private void StartBTConnect(@NonNull String strName) {
        DeviceName = strName.substring(0, strName.length() - 18);
        Toast.makeText(MainActivity.this, "Подключаюсь к " + DeviceName, Toast.LENGTH_SHORT).show();
        DeviceMAC = strName.substring(strName.length() - 17); // Вычленяем MAC-адрес
        con = Connection.Init();
        con.setInstance(BT.getInstance(DeviceMAC)); // new BT();
        con.addListener(MainActivity.this);
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
