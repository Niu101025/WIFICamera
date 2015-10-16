package com.ayit.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Toast.makeText(getApplicationContext(), sm.getSensorList(Sensor.TYPE_ALL).size() + "-", Toast.LENGTH_SHORT).show();
        textView = (TextView) findViewById(R.id.tvId);
        List<Sensor> sensorList = sm.getSensorList(Sensor.TYPE_ALL);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sensorList.size(); i++) {
            Sensor sensor = sensorList.get(i);
            builder.append(sensor.getName() + "\n");
        }
        textView.setText(builder.toString());

    }
}


