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

        ColorSensor color = null;
        color = hardwareMap.colorSensor.get("color");

        DcMotor motor;
        double  power = 0;
        motor = hardwareMap.get(DcMotor.class, "left_drive");

        telemetry.addData("Stats", "Initialized");
        telemetry.update();

        waitForStart();

        while(opModeIsActive()) {

            String probableColor = "";
            if(color.blue()>=2&&color.red()<2&&color.green()<2) probableColor="blue";
            else if(color.red()>=2&&color.blue()<2&&color.green()<2) probableColor="red";
            else probableColor="?";
            telemetry.addData("Color", "Probably "+probableColor);

            if(probableColor == "Probably red"){

                motor.setPower(power);

            }

            telemetry.update();
        }
    }
}
