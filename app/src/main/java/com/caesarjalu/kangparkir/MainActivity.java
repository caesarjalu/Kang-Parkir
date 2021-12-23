package com.caesarjalu.kangparkir;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anggastudio.printama.Printama;

public class MainActivity extends AppCompatActivity {
    TextView tvParkingCount;
    Button btnPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvParkingCount = findViewById(R.id.tv_parking_count);
        tvParkingCount.setText(String.valueOf(SharedPreferenceHandler.getParkingCount(getBaseContext())));
        btnPrint = findViewById(R.id.btn_print);
        findViewById(R.id.btn_printer_settings).setOnClickListener(v -> showPrinterList());
        findViewById(R.id.btn_print).setOnClickListener(v -> printTicket());
        findViewById(R.id.btn_reset).setOnClickListener(v -> resetParkingCount());

        getSavedPrinter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_setting) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void getSavedPrinter() {
        BluetoothDevice connectedPrinter = Printama.with(this).getConnectedPrinter();
        if (connectedPrinter != null) {
            TextView connectedTo = findViewById(R.id.tv_printer_info);
            String text = "Connected to : " + connectedPrinter.getName();
            connectedTo.setText(text);
        }
    }

    private void showPrinterList() {
        Printama.showPrinterList(this, R.color.colorBlue, printerName -> {
            Toast.makeText(this, printerName, Toast.LENGTH_SHORT).show();
            TextView connectedTo = findViewById(R.id.tv_printer_info);
            String text = "Connected to : " + printerName;
            connectedTo.setText(text);
            if (!printerName.contains("failed")) {
                findViewById(R.id.btn_printer_test).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_printer_test).setOnClickListener(v -> testPrinter());
            }
        });
    }

    private void testPrinter() {
        Printama.with(this).printTest();
    }

    private void printTicket() {
        int parkingCount = SharedPreferenceHandler.getParkingCount(getBaseContext()) + 1;
        String parkingName = SharedPreferenceHandler.getParkingName(getBaseContext());
        String parkingPrice = SharedPreferenceHandler.getParkingPrice(getBaseContext());

        try {
            Printama.with(this).connect(printama -> {
                if (printama.isConnected()) {
                    printama.printTextlnWideBold("=========================", Printama.CENTER);
                    printama.printTextlnTallBold(parkingName + "\n", Printama.CENTER);
                    printama.printTextlnWideTallBold(parkingCount + "\n", Printama.CENTER);
                    printama.printTextlnWideBold("Harga: Rp " + parkingPrice + "\n", Printama.CENTER);
                    printama.printTextlnWideBold("=========================", Printama.CENTER);
                    printama.addNewLine(2);
                    printama.close();
                    SharedPreferenceHandler.setParkingCount(getBaseContext(), parkingCount);
                    tvParkingCount.setText(String.valueOf(parkingCount));
                    showToast("Print berhasil");
                } else {
                    showToast("Gagal melakukan print, silahkan coba lagi");
                }
            }, this::showToast);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

    }

    private void resetParkingCount() {
        new AlertDialog.Builder(this)
                .setMessage("Apakah Anda yakin ingin melakukan reset?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferenceHandler.resetParkingCount(getBaseContext());
                        tvParkingCount.setText("0");
                        showToast("Reset Berhasil");
                    }})
                .setNegativeButton("Tidak", null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String printerName = Printama.getPrinterResult(resultCode, requestCode, data);
        showResult(printerName);
    }

    private void showResult(String printerName) {
        showToast(printerName);
        TextView connectedTo = findViewById(R.id.tv_printer_info);
        String text = "Connected to : " + printerName;
        connectedTo.setText(text);
        if (!printerName.contains("failed")) {
            findViewById(R.id.btn_printer_test).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_printer_test).setOnClickListener(v -> testPrinter());
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}