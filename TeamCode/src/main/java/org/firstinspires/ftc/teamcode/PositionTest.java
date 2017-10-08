package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.*;


@TeleOp
public class PositionTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        SensorManager sensorManager;
        sensorManager = (SensorManager) hardwareMap.appContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor gyro=sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyro != null) telemetry.addData("Sensor Found", "Yes");
        else telemetry.addData("Sensor Found", "No");

        telemetry.addData("Stats", "Initialized");
        telemetry.update();

        waitForStart();

        while(opModeIsActive()) {

            telemetry.update();
        }
    }
}
