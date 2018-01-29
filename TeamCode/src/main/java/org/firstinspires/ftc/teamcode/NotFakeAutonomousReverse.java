package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;


@Autonomous
public class NotFakeAutonomousReverse extends LinearOpMode {

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
        bottomColorSensor.setI2cAddress(I2cAddr.create8bit(0x42));
        alSensor.enableLed(true);
        bottomColorSensor.enableLed(true);

        // Al Arm
        Servo alArm=hardwareMap.get(Servo.class, "alArm");

        // Wrist rotation
        Servo wristRotate=hardwareMap.get(Servo.class, "wristRotate");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // ##################################################
        // #                      RUN                       #
        // ##################################################

        alArm.setPosition(0.04);

        wristRotate.setPosition(0.6);

        ArmUtil.armInit(); // Zero encoders and things


        // Jewel
        alArm.setPosition(0.7);
        sleep(1000);
        if((bottomColorSensor.red()>bottomColorSensor.blue())==(alSensor.red()>alSensor.blue())) { // Clockwise
            telemetry.addData("Color", "Clockwise");
            bl.setPower(-0.25);
            fl.setPower(-0.25);
            br.setPower(-0.25);
            fr.setPower(-0.25);
            sleep(500);
            bl.setPower(0.25);
            fl.setPower(0.25);
            br.setPower(0.25);
            fr.setPower(0.25);
        } else { // Counterclockwise
            telemetry.addData("Color", "Counterclockwise");
            bl.setPower(0.25);
            fl.setPower(0.25);
            br.setPower(0.25);
            fr.setPower(0.25);
            sleep(500);
            bl.setPower(-0.25);
            fl.setPower(-0.25);
            br.setPower(-0.25);
            fr.setPower(-0.25);
        }
        sleep(500);
        bl.setPower(0);
        fl.setPower(0);
        br.setPower(0);
        fr.setPower(0);
        alArm.setPosition(0.04);

        // Safe Zone

        //backwards
        bl.setPower(0.25);
        fl.setPower(0.25);
        //invert the right side
        br.setPower(-0.25);
        fr.setPower(-0.25);

        sleep(500);
        for(int x=0; x<300&&Math.abs(bottomColorSensor.red()-bottomColorSensor.green())+Math.abs(bottomColorSensor.blue()-bottomColorSensor.green())<3; x++) sleep(10);

        //stop moving the robot
        bl.setPower(0);
        fl.setPower(0);
        br.setPower(0);
        fr.setPower(0);

        sleep(1000);

        // Get arm ready for teleop
        ArmUtil.horizontalToPosition(0, 0.5);
        ArmUtil.verticalToPosition(-45, 0.1);
        // TODO something about the wrist?

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
