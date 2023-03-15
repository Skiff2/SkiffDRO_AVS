package com.home.skiffdro.common;

import androidx.appcompat.app.AppCompatActivity;

import com.home.skiffdro.models.Setts;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashSet;
import java.util.Set;

public class SettsActivity extends AppCompatActivity {

    //ActivitySettsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_setts);
        Setts.getInstance(this);
        //binding.setS(model);

        //binding.cmdSelDevices.setOnClickListener(v-> { SelBTDevice(); });
    }

    private void SelBTDevice() { // Создание списка сопряжённых Bluetooth-устройств
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(SettsActivity.this, android.R.layout.select_dialog_multichoice);
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for (BluetoothDevice device : pairedDevices) { // Добавляем сопряжённые устройства - Имя + MAC-адресс
            arrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }

       final AlertDialog dialog = new AlertDialog.Builder(SettsActivity.this)
                .setTitle("Выберите устройства:")
                .setAdapter(arrayAdapter, null)
                .setPositiveButton("Готово", (d, which) -> {

                    SparseBooleanArray positions = ((AlertDialog)d).getListView().getCheckedItemPositions();
                    Set<String> Lst = new HashSet<>();
                    for (int k = 0; k < arrayAdapter.getCount(); k++)
                        if (positions.get(k)) Lst.add(arrayAdapter.getItem(k));

                    Setts s = Setts.getInstance();
                    s.setBTDevicesList(Lst);

                    d.dismiss();
                    finish();
                })
                .setNegativeButton("Отмена", null)
                .create();

        dialog.getListView().setItemsCanFocus(false);
        dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        dialog.show();
    }
}