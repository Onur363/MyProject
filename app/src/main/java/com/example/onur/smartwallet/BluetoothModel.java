package com.example.onur.smartwallet;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.UUID;

public class BluetoothModel {
    private BluetoothSocket btSocket;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice btDevice;
    private String btAdress;
    private UUID btUUID;

    /*Bluetooth Adapter Encapsulasion */
    public BluetoothAdapter getBtAdapter() {
        return btAdapter;
    }
    public void setBtAdapter(BluetoothAdapter btAdapter) {
        this.btAdapter = btAdapter;
    }
 /*************************************************************/

    /***********BluetoothSocket encapsulation************/
    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    public void setBtSocket(BluetoothSocket btSocket) {
        this.btSocket = btSocket;
    }
    /******************************************************/

    /************** Bluetooth Device **************************/
    public BluetoothDevice getBtDevice() {
        return btDevice;
    }

    public void setBtDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

    /*****************************************************/

    /************** Bluetooth Adres ********************/

    public String getBtAdress() {
        return btAdress;
    }

    public void setBtAdress(String btAdress) {
        this.btAdress = btAdress;
    }
    /***************************************************/

    /***************BT UUUID ******************************/

    public UUID getBtUUID() {
        return btUUID;
    }

    public void setBtUUID(UUID btUUID) {
        this.btUUID = btUUID;
    }

    /*********************************************************/

}
