package com.example.Vending_Machine;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

public class VendingMachine {

    public static UUID VENDING_SERVICE = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb");
    /* Mandatory Current Time Information Characteristic */
    public static UUID READ_WRITE_VAR = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb");
    /* Mandatory Client Characteristic Config Descriptor */
    //public static UUID CLIENT_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    /* Var to read/write */
    public static String price;

    public static BluetoothGattService createVendingMachineService() { BluetoothGattService service = new BluetoothGattService(VENDING_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        // Current Time characteristic
        BluetoothGattCharacteristic currentTime = new BluetoothGattCharacteristic(READ_WRITE_VAR,
                //Read-only characteristic, supports notifications
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PERMISSION_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ |BluetoothGattCharacteristic.PERMISSION_WRITE );

        service.addCharacteristic(currentTime);

        return service;
    }

    public static byte[] readwrite_request() {

        byte[] b = price.getBytes();

        return b;
    }

    public static void  set_var(String value) {

        price = value;

        return ;
    }

}