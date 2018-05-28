package com.example.onur.smartwallet;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static android.support.design.widget.Snackbar.make;

public class ledControl extends AppCompatActivity {

    Button btnOn, btnOff, btnDis;
    TextView lumn;
    Switch btnServis;
    ProgressDialog progress;
    public BluetoothDevice btDevice;
    public BluetoothAdapter myBluetooth = null;
    public BluetoothSocket btSocket = null;
    public  static String address;
    public boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothModel btModel=new BluetoothModel();
    LinearLayout linearLayout;

    @Override
    protected void onResume() {
        super.onResume();

        btnServis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                linearLayout=findViewById(R.id.linearlayout);
                final Timer timer=new Timer();

                if(isChecked){
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                btSocket.getOutputStream().write("X".toString().getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                                alertnot();
                                timer.cancel();
                            }
                        }
                    },0,2000);

                    final Snackbar snackbar=make(linearLayout,"Cüzdan üzerinden Street Modu açmayı unutmayın",Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    snackbar.setAction("TAMAM", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                }
                else{
                    timer.cancel();
                    try {
                        btSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }
    public void bildirim()
    {
        AlertDialog.Builder alert=new AlertDialog.Builder(ledControl.this);
        alert.setTitle("SmartWallet");
        alert.setMessage("Bağlantınız koptu lütfen tekrar bağlanmayı deneyin");
        alert.setNeutralButton("TAMAM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new ConnectBT().execute();
            }
        });
        alert.show();
    }

    public void alertnot(){

        int icon=R.drawable.ic_launcher_foreground;
        long when=System.currentTimeMillis();
        String baslik="Smart Wallet";
        NotificationManager nm= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent=new Intent(getApplicationContext(),DeviceList.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(), (int) when,intent,0);

        Notification notification= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(getApplicationContext()).setSmallIcon(icon)
                    .setContentTitle(baslik)
                    .setContentText(String.valueOf("Bağlantınız koptu lütfen yeniden bağlanmayı deneyin"))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true).build();
        }


        notification.flags=Notification.FLAG_AUTO_CANCEL;
        notification.defaults=Notification.DEFAULT_SOUND;
        notification.defaults=Notification.DEFAULT_VIBRATE;
        nm.notify(0,notification);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.Adress);

        btnOn = (Button) findViewById(R.id.button2);
        btnOff = (Button) findViewById(R.id.button3);
        btnDis = (Button) findViewById(R.id.button4);
        lumn = (TextView) findViewById(R.id.lumn);
        btnServis = (Switch) findViewById(R.id.button5);

         new ConnectBT().execute();


        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnLed();
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffLed();
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });


    }

    private void Disconnect()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().close();

            }
            catch (IOException e)
            {

            }
        }

        finish();

    }

    private void turnOffLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("K".toString().getBytes());

            }
            catch (IOException e)
            {
                alertnot();
            }
        }
    }

    private void turnOnLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("A".toString().getBytes());

            }
            catch (IOException e)
            {

                alertnot();
            }
        }
    }


    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
    }
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ledControl.this, "Bağlantı kuruluyor...", "Lütfen bekelyin!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                    if(btSocket==null || !isBtConnected)
                    {
                        btModel.setBtUUID(myUUID);
                        myBluetooth = BluetoothAdapter.getDefaultAdapter();
                        btModel.setBtAdapter(myBluetooth);
                        btModel.setBtAdress(address);
                        btDevice = myBluetooth.getRemoteDevice(btModel.getBtAdress());
                        btModel.setBtDevice(btDevice);
                        btSocket = btModel.getBtDevice().createInsecureRfcommSocketToServiceRecord(btModel.getBtUUID());
                        btModel.setBtSocket(btSocket);
                        btModel.getBtAdapter().getDefaultAdapter().cancelDiscovery();
                        btModel.getBtSocket().connect();
                    }

            } catch (IOException e) {
                    ConnectSuccess=false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(!ConnectSuccess){
                msg("Bağlantı kurulurken hata oluştu");
                finish();
            }
            else{
                msg("Bağlantı kuruldu");
            }

            progress.dismiss();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.walletmenu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_reconnect :
                if (btSocket!=null)
                {
                    try
                    {
                        btSocket.getOutputStream().close();

                    }
                    catch (IOException e)
                    {

                    }

                }

                finish();

            case R.id.menu_renew: new ConnectBT().execute();

        }
        return  false;
    }
}
