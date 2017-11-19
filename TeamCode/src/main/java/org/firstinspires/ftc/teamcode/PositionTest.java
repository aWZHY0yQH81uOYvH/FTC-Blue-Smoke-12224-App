package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.*;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*

This talks to the phone's accelerometer and reads out the data
The phones we have don't have a gyroscope, which was probably intentional

 */

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
            // Accelerometer values in m/s^2, need to have a calibration period where it takes a bunch of readings
            // over a period of time and averages them, then offsets each reading by that much, and integrates it
            // at a precise sample rate to (possibly) get a kinda crappy idea of the position of the robot...
            // Hopefully it'll be good enough to get around in autonomous mode.
            // Will likely have to use the Modern Robotics gyro too
            // IDK why I'm using a bunch of single-line comments...
            telemetry.addData("X", vals[0]);
            telemetry.addData("Y", vals[1]);
            telemetry.addData("Z", vals[2]);

            telemetry.update();
        }
    }
}
