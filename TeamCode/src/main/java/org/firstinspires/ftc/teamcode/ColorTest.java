package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp
public class ColorTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        //this is for the color sensor

        ColorSensor color;
        color = hardwareMap.colorSensor.get("color");
        color.enableLed(true);

        //this is for the motor

        DcMotor motor;
        motor = hardwareMap.get(DcMotor.class, "motorr");
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Stats", "Initialized");
        telemetry.update();

        waitForStart();

        while(opModeIsActive()) {
            telemetry.addData("Redness", color.red());
            if(color.red()>0.5) motor.setPower(1);
            else motor.setPower(0);

            telemetry.update();
        }
    }
}
