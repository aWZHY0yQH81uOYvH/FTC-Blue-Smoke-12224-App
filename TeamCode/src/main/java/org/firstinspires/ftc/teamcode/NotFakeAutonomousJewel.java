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
public class NotFakeAutonomousJewel extends LinearOpMode {

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
        ChassisUtil.fl=hardwareMap.get(DcMotor.class, "fl");
        ChassisUtil.fr=hardwareMap.get(DcMotor.class, "fr");
        ChassisUtil.bl=hardwareMap.get(DcMotor.class, "bl");
        ChassisUtil.br=hardwareMap.get(DcMotor.class, "br");

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

        ChassisUtil.init();

        alArm.setPosition(0.04);

        wristRotate.setPosition(0.6);


        // Jewel
        int red = 0, blue = 0;
        alArm.setPosition(0.60);
        sleep(1000); // TODO put these changes in the other ones
        red+= alSensor.red();
        blue+= alSensor.blue();
        for(double i=0.61; i<=0.7; i+=0.01) {
            alArm.setPosition(i);
            sleep(50);
            red+= alSensor.red();
            blue+= alSensor.blue();
        }

        telemetry.addData("Al Red", red);
        telemetry.addData("Al Blue", blue);

        if(red>blue&&bottomColorSensor.red()>bottomColorSensor.blue()) {
            telemetry.addData("Color", "Clockwise");
            ChassisUtil.setPower(0.25, -0.25);
            sleep(500);
            ChassisUtil.setPower(-0.25, 0.25);

        } else if(red<blue&&bottomColorSensor.red()<bottomColorSensor.blue()) { // Counterclockwise
            telemetry.addData("Color", "Counterclockwise");
            ChassisUtil.setPower(-0.25, 0.25);
            sleep(500);
            ChassisUtil.setPower(0.25, -0.25);
        } else {
            telemetry.addData("fuck", true);
        }
        sleep(550);
        ChassisUtil.setPower(0);

        /*
        if(alSensor.red()!=alSensor.blue()) {
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
            sleep(550);
            bl.setPower(0);
            fl.setPower(0);
            br.setPower(0);
            fr.setPower(0);

        }
        */
        alArm.setPosition(0.04);


        /*Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

            }
        }, 0, 10); // 10ms*/

        telemetry.update();

        while(opModeIsActive()) {}

        //ArmUtil.stop();
        ChassisUtil.stop();
        //timer.cancel();
    }
}
