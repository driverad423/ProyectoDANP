package com.example.proyectodanp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final static int NOTIFICATION_ID=0;
    SensorManager sensorManager;
    Sensor sensor_proximidad;
    int contador=0;
    double prox_distancia=0, luz=0;
    TextView proximidad, distancia;
    String str_proximidad="LEJOS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        proximidad= findViewById(R.id.proximidad);
        distancia= findViewById(R.id.distancia);
        //inicio de sensormanager
        sensorManager= (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //definiciom de los sensores usados
        sensor_proximidad=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);


    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType()==Sensor.TYPE_PROXIMITY){
            prox_distancia=event.values[0];
            distancia.setText("");
            distancia.append(""+prox_distancia);

            if(prox_distancia<1){
                str_proximidad="CERCA";
                createNotificationChannel();
                crearNotificacion();

            }else{
                str_proximidad="LEJOS";
            }
            proximidad.setText("");
            proximidad.append(""+str_proximidad);

        }
        contador++;

        //runOnUiThread(new CambiarTexto());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor_proximidad, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private class CambiarTexto implements Runnable {
        public void run() {
            distancia.setText(""+prox_distancia);
            proximidad.setText(""+str_proximidad);
        }
    }

    private void createNotificationChannel(){
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        long[] vibrationWaveFormDurationPattern = {1000, 1000, 1000};

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            VibrationEffect vibrationEffect = VibrationEffect.createWaveform(vibrationWaveFormDurationPattern, -1);

            // it is safe to cancel all the vibration taking place currently
            vibrator.cancel();

            // now initiate the vibration of the device
            vibrator.vibrate(vibrationEffect);
            CharSequence name = "Noticacion";
            NotificationChannel notificationChannel = new NotificationChannel("NOTIFICACION", name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    private void crearNotificacion(){
        NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplicationContext(), "NOTIFICACION");
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setSmallIcon(R.drawable.ic_warning);
        builder.setContentTitle("APP PROYECTO DANP");
        builder.setContentText("El dispositivo esta boca abajo");
        builder.setColor(Color.CYAN);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());

    }
}