package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.*;

import org.firstinspires.ftc.robotcore.external.Telemetry;


@TeleOp
public class PositionTest extends LinearOpMode implements SensorEventListener {

    private float vals[]={0,0,0};
    private int cals=0;
    private boolean init=true;

    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) vals=event.values.clone();
    }

    public void onAccuracyChanged(Sensor x, int y) {}

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        SensorManager sensorManager;
        sensorManager = (SensorManager) hardwareMap.appContext.getSystemService(Context.SENSOR_SERVICE);
        telemetry.addData("Sensors", sensorManager.getSensorList(Sensor.TYPE_ALL));
        Sensor acc=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (acc != null) telemetry.addData("Sensor Found", "Yes");
        else telemetry.addData("Sensor Found", "No");
        sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);

        init=false;
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while(opModeIsActive()) {

            telemetry.addData("X", vals[0]);
            telemetry.addData("Y", vals[1]);
            telemetry.addData("Z", vals[2]);

            telemetry.update();
        }
    }
}
