package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Timer;
import java.util.TimerTask;


@Autonomous
public class NotFakeAutonomous extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        // ##################################################
        // #                      INIT                      #
        // ##################################################

        telemetry.addData("Status", "Initializing");
        telemetry.update();

        // Give ArmUtil things
        ArmUtil.horizontalTurret=hardwareMap.get(DcMotor.class, "horizontalTurret");
        ArmUtil.verticalTurret=hardwareMap.get(DcMotor.class, "verticalTurret");
        ArmUtil.wristWinch=hardwareMap.get(DcMotor.class, "wristWinch");
        ArmUtil.extendoMotor=hardwareMap.get(DcMotor.class, "extendo");
        ArmUtil.horizontalLimit=hardwareMap.get(ModernRoboticsTouchSensor.class, "horizontalLimit");
        ArmUtil.verticalLimit=hardwareMap.get(ModernRoboticsTouchSensor.class, "verticalLimit");
        ArmUtil.wristLimit=hardwareMap.get(ModernRoboticsTouchSensor.class, "wristLimit");
        ArmUtil.grabberLimit=hardwareMap.get(ModernRoboticsTouchSensor.class, "grabberLimit");
        ArmUtil.wristHorizontal=hardwareMap.get(Servo.class, "wristHorizontal");
        ArmUtil.grabber=hardwareMap.get(CRServo.class, "grabber");

        // Chassis
        DcMotor bl=hardwareMap.get(DcMotor.class, "bl"), br=hardwareMap.get(DcMotor.class, "br"), fl=hardwareMap.get(DcMotor.class, "fl"), fr=hardwareMap.get(DcMotor.class, "fr");
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Color sensors
        ColorSensor alSensor=hardwareMap.get(ColorSensor.class, "alSensor");
        ColorSensor bottomColorSensor=hardwareMap.get(ColorSensor.class, "bottomSensor");
        alSensor.enableLed(true);
        bottomColorSensor.enableLed(true);

        // Al Arm
        Servo alArm=hardwareMap.get(Servo.class, "alArm");
        alArm.setPosition(0.04);

        // used to determine which side the robot is on


        // Wrist rotation
        //Servo wristRotate=hardwareMap.get(Servo.class, "wristRotate");
        //wristRotate.setPosition(0.6);

        //ArmUtil.armInit(); // Zero encoders and things

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // ##################################################
        // #                      RUN                       #
        // ##################################################



        // Jewel
        alArm.setPosition(0.7);
        sleep(1000);
        if(bottomColorSensor.argb()<=6&&alSensor.argb()<=6) { // Clockwise
            telemetry.addData("Color", "Clockwise");
            bl.setPower(0.25);
            fl.setPower(0.25);
            br.setPower(0.25);
            fr.setPower(0.25);
        } else { // Counterclockwise
            telemetry.addData("Color", "Counterclockwise");
            bl.setPower(-0.25);
            fl.setPower(-0.25);
            br.setPower(-0.25);
            fr.setPower(-0.25);
        }
        sleep(250);
        bl.setPower(0);
        fl.setPower(0);
        br.setPower(0);
        fr.setPower(0);
        alArm.setPosition(0.04);

        // Safe Zone

        //find which side the robot is on
        if(bottomColorSensor.blue() >= (bottomColorSensor.red() * 5)) {

            //move the robot from the blue side
            //forwards
            bl.setPower(0.25);
            fl.setPower(0.25);
            //invert the right side
            br.setPower(-0.25);
            fr.setPower(-0.25);


        } else{

            //move the robot from the red side
            //backwards
            bl.setPower(-0.25);
            fl.setPower(-0.25);
            //invert the right side
            br.setPower(0.25);
            fr.setPower(0.25);

        }

        //stop moving the robot
        sleep(5000); // Should change this to stop when it sees the lines with the color sensor
        bl.setPower(0);
        fl.setPower(0);
        br.setPower(0);
        fr.setPower(0);


        /*Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

            }
        }, 0, 10); // 10ms*/

        while(opModeIsActive()) {

            telemetry.update();
        }
        ArmUtil.stop();
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        //timer.cancel();
    }
}
