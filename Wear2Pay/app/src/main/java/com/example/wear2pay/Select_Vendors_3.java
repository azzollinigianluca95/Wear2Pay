package com.example.wear2pay;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.Wear2Pay.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Select_Vendors_3 extends WearableActivity {

    private TextView scan_vm;
    ImageButton annulla;
    TextView t ;
    ListView TransactionList;

    //**
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;

    private static final long SCAN_PERIOD = 5000;     // Stops scanning after 10 seconds. 10000

    private  LeDeviceListAdapter leDeviceListAdapter;
    private  LeDeviceListAdapter leDeviceListAdapter_n;

    private static final int REQUEST_ENABLE_BT = 1;
    private List<ScanFilter> filters;
    public ScanSettings settings;
    private BluetoothLeScanner mLEScanner;
    private BluetoothGatt mGatt;
    private static String device_t;


    private static final int LOCATION_PERMISSION_CODE = 121;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    /* Current Time Service UUID */
    public static UUID VENDING_SERVICE = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb");
    /* Mandatory Current Time Information Characteristic */
    public static UUID READ_WRITE_VAR = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb");
    public String user_id;
    private BluetoothGattDescriptor bluetoothGattDescriptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.select_vendors3);
        scan_vm = (TextView) findViewById(R.id.scan_vm);
        TransactionList = (ListView) findViewById(R.id.machine_id);
        user_id  = getIntent().getStringExtra("user_id");

        t = (TextView) findViewById(R.id.scan_vm); //associo all'oggetto presentation_text l'id del testo

        Animation a = AnimationUtils.loadAnimation( Select_Vendors_3.this, R.anim.blink_anim); //Genero animazione
        t.startAnimation(a);

        annulla = (ImageButton) findViewById(R.id.annulla);

        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
                //Toast.makeText(Activity2.this, "si", Toast.LENGTH_SHORT).show(); //CREATE AND SHOW A TOAST
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported", Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        handler = new Handler(); // UTILIZZATO PER IMPOSTARE UN TIMEOUT

        leDeviceListAdapter = new LeDeviceListAdapter();
        leDeviceListAdapter_n = new LeDeviceListAdapter();
        checkLocationPermission();
        startScan();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                    //scanLeDevice(false);
                    for (int i=0; i < leDeviceListAdapter.getCount(); i++ ) {
                        BluetoothDevice b = leDeviceListAdapter.getDevice(i);
                        if (b.getName() != null)
                        {
                            leDeviceListAdapter_n.addDevice(b);
                            Log.e("ADD TO NEW LIST", b.getName()); //print all the scanned devices
                        }
                    }
                }
                //list view of scanned devices
                ArrayList<String> arrayList = new ArrayList<>();
                for (int i=0; i < leDeviceListAdapter_n.getCount(); i++ )
                {
                    BluetoothDevice b = leDeviceListAdapter_n.getDevice(i);
                    arrayList.add(  b.getName() );
                }
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(Select_Vendors_3.this, R.layout.custom_textview,arrayList);
                TransactionList.setAdapter(arrayAdapter);
                TransactionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BluetoothDevice b = leDeviceListAdapter_n.getDevice(position);
                        String s = "Effettuo connessione a "+ b.getName()+"\n Attendere prego";
                        Toast.makeText(Select_Vendors_3.this,s, Toast.LENGTH_LONG).show();
                        //make connection
                        connectToDevice(b);
                    }
                });

            }
        }, 5000);
    }


    @Override
    protected void onStart() { super.onStart(); }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void startScan() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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

    @Override
    protected void onPause()
    {
        super.onPause();
        //mGatt.close();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mGatt == null) {
            return;
        }
        mGatt.close(); //PROVA DISCONNECT
        mGatt = null;
    }



    //Red Cross Button
    public void openActivity2 () {
        Intent intent = new Intent(Select_Vendors_3.this, Main_Activity_2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("user_id",user_id);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //BLE Functions
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLEScanner.stopScan( mleScanCallback);

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
            leDeviceListAdapter.addDevice(btDevice);
            leDeviceListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());            }
        }
        @Override
        public void onScanFailed(int errorcode) {
            // codice da eseguire
            Log.e("Scan Failed", "Error Code: " + errorcode);        }
    };

    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            device_t = device.getName();
            Log.e("ADDRESS", device_t);
            mGatt = device.connectGatt(this, false, gattCallback); //PROVA TRUE
            scanLeDevice(false);// will stop after first device detection
        }
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
            Log.i("onServicesDiscovered", services.toString());
            //LETTURA
            //gatt.readCharacteristic(gatt.getService(VENDING_SERVICE).getCharacteristic(READ_WRITE_VAR));

            //SCRITTURA

            BluetoothGattCharacteristic characteristic1 = gatt.getService(VENDING_SERVICE).getCharacteristic(READ_WRITE_VAR);

            if ((characteristic1.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0)
            {
                Log.d("Permessi Char. Server", "OK");
            }
            else
            {
                Log.e("Permessi Char. Server", "NEGATI!");
            }
                    //Write on Gatt Server
                    byte[] userid = user_id.getBytes();
                    boolean result;
                    BluetoothGattService mSVC = gatt.getService(VENDING_SERVICE);
                    BluetoothGattCharacteristic mCH = mSVC.getCharacteristic(READ_WRITE_VAR);
                    gatt.setCharacteristicNotification(mCH, true);
                    mCH.setValue(userid);
                    result = gatt.writeCharacteristic(mCH);
                    gatt.disconnect();

                    if (result) {
                        Log.d("BLE Write", "SUCCESS!");
                    }
                    else {
                        Log.e("BLE Write", "FAILED!");
                    }

                    Intent intent = new Intent(Select_Vendors_3.this, Select_Product_5.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("user_id",user_id);
                    startActivity(intent);
                    finish();

        }




        //SERVE A LETTURA DEL PREZZO
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            Log.i("onCharacteristic_uuid", String.valueOf(characteristic.getUuid()) );
            int flag = characteristic.getProperties();
            Log.d("Char. Properties: ", String.valueOf(flag));
            final byte[] data = characteristic.getValue();

            if (data != null && data.length > 0)
            {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));

                Log.e("DATA", new String(data) + "\n" + stringBuilder.toString() );
            }
        }
    };

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


    //LeDeviceListAdapter
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;
        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = Select_Vendors_3.this.getLayoutInflater();
        }
        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }
        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }
        public void clear() {
            mLeDevices.clear();
        }
        @Override
        public int getCount() {
            return mLeDevices.size();
        }
        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        public View getView(int i, View view, ViewGroup viewGroup) {
            return view;

        }
    }


    public static String getDevice_name()
    {
        return device_t;
    }

}