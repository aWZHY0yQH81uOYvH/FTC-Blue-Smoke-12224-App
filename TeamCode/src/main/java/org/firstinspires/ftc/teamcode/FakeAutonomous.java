package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Timer;
import java.util.TimerTask;


@Autonomous
public class FakeAutonomous extends LinearOpMode {

    private static int boolToInt(boolean in) {
        if(in) return -1;
        else return 1;
    }

    boolean zeroRegister=false, stabilize=true;
    int crush=0, lastVPos=0, lastHPos=0;
    double userHWrist=0;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        // Give ArmUtil things
        ArmUtil.horizontalTurret=hardwareMap.get(DcMotor.class, "horizontalTurret");
        ArmUtil.verticalTurret=hardwareMap.get(DcMotor.class, "verticalTurret");
        ArmUtil.wristWinch=hardwareMap.get(DcMotor.class, "wristWinch");
        ArmUtil.horizontalLimit=hardwareMap.get(ModernRoboticsTouchSensor.class, "horizontalLimit");
        ArmUtil.verticalLimit=hardwareMap.get(ModernRoboticsTouchSensor.class, "verticalLimit");
        ArmUtil.wristLimit=hardwareMap.get(ModernRoboticsTouchSensor.class, "wristLimit");
        ArmUtil.grabberLimit=hardwareMap.get(ModernRoboticsTouchSensor.class, "grabberLimit");
        ArmUtil.wristHorizontal=hardwareMap.get(Servo.class, "wristHorizontal");
        ArmUtil.grabber=hardwareMap.get(CRServo.class, "grabber");

        if(!ArmUtil.armInit()) {
            telemetry.addData("Status", "INIT ERROR");
            telemetry.update();
            sleep(1000);
            return;
        }

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        ArmUtil.stow();
        ArmUtil.stop();

        telemetry.addData("Status", "Stowed");
        telemetry.update();

        waitForStart();

        /*ArmUtil.verticalToPosition(45, 0.1);
        ArmUtil.winchToPosition(0, 0.5);
        sleep(8000);
        ArmUtil.horizontalToPosition(-45, 0.1);
        ArmUtil.verticalToPosition(-20, 0.1);*/

        while(opModeIsActive()) {

        }
        ArmUtil.stop();
    }
}
