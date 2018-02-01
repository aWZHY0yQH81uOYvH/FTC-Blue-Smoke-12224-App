package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Timer;
import java.util.TimerTask;


@TeleOp
public class ExtendoMove extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        // ##################################################
        // #                      INIT                      #
        // ##################################################

        telemetry.addData("Status", "Initializing");
        telemetry.update();

        DcMotor extendo=hardwareMap.get(DcMotor.class, "extendo");
        extendo.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        extendo.setDirection(DcMotorSimple.Direction.FORWARD);

        DcMotor wristWinch=hardwareMap.get(DcMotor.class, "wristWinch");
        wristWinch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        wristWinch.setDirection(DcMotorSimple.Direction.FORWARD);

        int extendoOffset=extendo.getCurrentPosition();

        AnalogInput lengthSensor=hardwareMap.get(AnalogInput.class, "lengthSensor");

        ColorSensor alSensor=hardwareMap.get(ColorSensor.class, "alSensor");
        ColorSensor bottomColorSensor=hardwareMap.get(ColorSensor.class, "bottomSensor");
        bottomColorSensor.setI2cAddress(I2cAddr.create8bit(0x42));
        alSensor.enableLed(true);
        bottomColorSensor.enableLed(true);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        Servo alArm=hardwareMap.get(Servo.class, "alArm");

        waitForStart();





        // ##################################################
        // #                      RUN                       #
        // ##################################################

        while(opModeIsActive()) {

            if(gamepad1.dpad_up) extendo.setPower(1);
            else if(gamepad1.dpad_down) extendo.setPower(-1);
            else extendo.setPower(0);

            if(gamepad2.dpad_up) wristWinch.setPower(0.25);
            else if(gamepad2.dpad_down) wristWinch.setPower(-0.25);
            else wristWinch.setPower(0);

            alArm.setPosition(Math.abs(gamepad2.left_stick_y));

            telemetry.addData("Servo Position", Math.abs(gamepad2.left_stick_y));

            telemetry.addData("Length Sensor", lengthSensor.getVoltage());
            telemetry.addData("Encoder", extendo.getCurrentPosition()-extendoOffset);

            telemetry.addData("Bottom Red", bottomColorSensor.red());
            telemetry.addData("Bottom Green", bottomColorSensor.green());
            telemetry.addData("Bottom Blue", bottomColorSensor.blue());
            telemetry.addData("Bottom Alpha", bottomColorSensor.alpha());

            telemetry.addData("Al's Red", alSensor.red());
            telemetry.addData("Al's Green", alSensor.green());
            telemetry.addData("Al's Blue", alSensor.blue());
            telemetry.addData("Al's Alpha", alSensor.alpha());

            telemetry.addData("Al's Red ARGB", alSensor.argb()&255);
            telemetry.addData("Al's Green ARGB", (alSensor.argb()>>8)&255);
            telemetry.addData("Al's Blue ARGB", (alSensor.argb()>>16)&255);
            telemetry.addData("Al's Alpha ARGB", (alSensor.argb()>>24)&255);

            telemetry.update();
        }
        extendo.setPower(0);
    }
}
