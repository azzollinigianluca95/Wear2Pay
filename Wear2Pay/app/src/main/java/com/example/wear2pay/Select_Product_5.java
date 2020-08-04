package com.example.wear2pay;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.Wear2Pay.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class Select_Product_5 extends WearableActivity {

    private ImageButton annulla;
    private Timer timer;
    private TextView connection_done; //Dichiaro oggetto di tipo testo
    private TextView t_select_product ;
    /* Current Time Service UUID */
    public static UUID VENDING_SERVICE = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb");
    /* Mandatory Current Time Information Characteristic */
    public static UUID READ_WRITE_VAR = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb");
    public static UUID CLIENT_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private BluetoothGatt gatt;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bd;
    private Handler handler;
    private static final int REQUEST_ENABLE_BT = 1;
    private List<ScanFilter> filters;
    public ScanSettings settings;
    private BluetoothLeScanner mLEScanner;
    private String address;
    private static final long SCAN_PERIOD = 8000;     // Stops scanning after 10 seconds. 10000
    private static final int LOCATION_PERMISSION_CODE = 121;
    public String result = "0";
    private String user_id;
    public boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_product5);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        user_id = getIntent().getStringExtra("user_id");
        address = Select_Vendors_3.getDevice_name();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        connection_done = (TextView) findViewById(R.id.connection_done); //associo all'oggetto presentation_text l'id del testo
        t_select_product = (TextView) findViewById(R.id.SelectProduct); //associo all'oggetto presentation_text l'id del testo

        Animation a1 = AnimationUtils.loadAnimation( Select_Product_5.this, R.anim.zoomin_mod); //Genero animazione
        Animation a2 = AnimationUtils.loadAnimation( Select_Product_5.this, R.anim.fadein); //Genero animazione

        connection_done.startAnimation(a1);
        t_select_product.startAnimation(a2);

        handler = new Handler(); // UTILIZZATO PER IMPOSTARE UN TIMEOUT


        annulla = (ImageButton) findViewById(R.id.annulla);

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Log.e("TIMEOUT","END");
                //Lettura  da BLE Server
                final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                bluetoothAdapter = bluetoothManager.getAdapter();
                checkLocationPermission();
                startScan();

            }
        },12000);
    }

    public void openActivity2 () {
        Intent intent = new Intent(Select_Product_5.this, Main_Activity_2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("user_id",user_id);
        startActivity(intent);
        timer.cancel();
        finish();
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.d("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.d("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.e("onServicesDiscovered", services.toString());

            //LETTURA
            gatt.readCharacteristic(gatt.getService(VENDING_SERVICE).getCharacteristic(READ_WRITE_VAR));
        }

        //SERVE A LETTURA DEL PREZZO
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            Log.i("onCharacteristic_uuid", String.valueOf(characteristic.getUuid()));
            int flag = characteristic.getProperties();
            Log.d("Char. Properties: ", String.valueOf(flag));
            final byte[] data = characteristic.getValue();

            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));

                result = new String(data);
                Log.e("DATA", result);

                transaction_esit(result);
            }
            if (data == null) {
                Log.e("DATA EMPTY", "DATI VUOTI");
            }
        }
    }; //Closing Gatt Callback

    void connect()
    {
        gatt = bd.connectGatt(Select_Product_5.this, false, gattCallback,BluetoothDevice.TRANSPORT_LE);
    }

    void connect_close(){
        gatt.disconnect();
        //gatt.close();
    }

    protected void startScan() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT );
        } else {
            mLEScanner = bluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<ScanFilter>();
            scanLeDevice(true);
            Log.d("SCANLEDEVICE","TRUE");
        }
    }


    //BLE Functions
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("SCAN","STOP");
                    mLEScanner.stopScan( mleScanCallback);
                    if (connected == false)
                    {
                        if (!result.equals("-1.0"))
                        {
                            result = "-1.0";
                            transaction_esit(result);
                        }
                    }


                }
            }, SCAN_PERIOD);

            mLEScanner.startScan(   filters, settings, mleScanCallback);
        } else {
            mLEScanner.stopScan( mleScanCallback);
        }
    }

    // Device scan callback.
    private ScanCallback mleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType , ScanResult result) {
            // codice da eseguire
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            if (result.getDevice().getName() != null)
            {
                Log.d("SCANNED:", result.getDevice().getName()); //print all the scanned devices with name
            }

            BluetoothDevice btDevice = result.getDevice();

            if (btDevice.getName() != null) {
                if (btDevice.getName().equals(address)) {
                    connectToDevice(btDevice);
                    connected = true;
                } else {
                    Log.e("NOT EQUALS ADD", address);
                    Log.e("NOT EQUALS btde", btDevice.getAddress());

                }
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }
        @Override
        public void onScanFailed(int errorcode) {
            // codice da eseguire
            Log.e("Scan Failed", "Error Code: " + errorcode);        }
    };

    public void connectToDevice(BluetoothDevice device) {
        if (gatt == null) {
            Log.e("ADDRESS", device.getAddress());
            gatt = device.connectGatt(this, false, gattCallback); //PROVA TRUE
            scanLeDevice(false);// will stop after first device detection
        }
    }

    private void checkLocationPermission() {
        if(isReadStorageAllowed()){
            //If permission is already having then showing the toast
            //Toast.makeText(Select_Vendors_3.this,"You already have the permission",Toast.LENGTH_LONG).show();
            //Existing the method with return
            startScan();
            return;
        }
        //If the app has not the permission then asking for the permission
        requestStoragePermission();
    }

    //Requesting permission
    private void requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_PERMISSION_CODE);
    }

    //We are calling this method to check the permission status
    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if(requestCode == LOCATION_PERMISSION_CODE){
            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startScan();
                //Displaying a toast
                Toast.makeText(this,"Permessi concessi",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Permessi non concessi",Toast.LENGTH_LONG).show();
            }
        }
    }

    void transaction_esit(String result)
    {

        if ( result.equals("-2.0") )
        {
            connect_close();
            //Unsufficient credit
            Log.e("Transaction","Unsufficient credit");
            Intent intent = new Intent(Select_Product_5.this, Unsufficient_Credit_6b.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("user_id",user_id);
            startActivity(intent);
            finish();
        }
        else if(result.equals("-1.0") )
        {
            //Connection Error
            Log.e("Transaction","Error Connection");
            Intent intent = new Intent(Select_Product_5.this, Connection_Timeout_6c.class); //timeout o errore di connessione
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("user_id",user_id);
            startActivity(intent);
            finish();
        }
        else
        {
            connect_close();
            //Transaction Ok
            Log.e("Transaction","DONE");
            Intent intent = new Intent(Select_Product_5.this, Purchase_Done_6a.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("user_id",user_id);
            intent.putExtra("price", result);
            startActivity(intent);
            finish();
        }
    }
    public void onDestroy() {

        super.onDestroy();

        if (gatt == null) {
            return;
        }
        gatt.close(); //PROVA DISCONNECT
        gatt = null;

    }
}


