package com.example.onur.smartwallet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class BtService extends Service {

    BluetoothModel bluetoothModel=new BluetoothModel();
    Timer timer=new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        try {

                            bluetoothModel.getBtSocket().getOutputStream().write("X".toString().getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                            alertnot();
                            timer.cancel();
                        }
                    }
                },0,2000);

        return START_STICKY;
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
}
