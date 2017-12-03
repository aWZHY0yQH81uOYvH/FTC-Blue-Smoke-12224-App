package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Timer;
import java.util.TimerTask;


@TeleOp
public class DriverControl extends LinearOpMode {

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

        // Chassis
        DcMotor bl, br, fl, fr;
        bl = hardwareMap.get(DcMotor.class, "bl");
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br = hardwareMap.get(DcMotor.class, "br");
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fl = hardwareMap.get(DcMotor.class, "fl");
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fr = hardwareMap.get(DcMotor.class, "fr");
        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        if(!ArmUtil.armInit()) {
            telemetry.addData("Status", "INIT ERROR");
            telemetry.update();
            sleep(1000);
            return;
        }

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                userHWrist=ArmUtil.limitHWrist(userHWrist-Math.pow(gamepad1.right_stick_x, 5)/2.0);
            }
        }, 0, 10); // 10ms

        while(opModeIsActive()) {
            telemetry.addData("Vertical Pos", ArmUtil.vPos);
            telemetry.addData("Vertical Pow", ArmUtil.vPower);
            telemetry.addData("Horizontal Pos", ArmUtil.hPos);
            telemetry.addData("Horizontal Pow", ArmUtil.hPower);
            telemetry.addData("HWrist Pos", userHWrist);
            telemetry.addData("VWrist Pos", ArmUtil.winchPos);
            telemetry.addData("VWrist Pow", ArmUtil.winchPower);

            if(gamepad1.left_stick_button) { // Center with joystick button
                if(!zeroRegister) {
                    if(ArmUtil.hPos==0) ArmUtil.verticalToPosition(0, 0.5);
                    else ArmUtil.horizontalToPosition(0, 0.3);
                    zeroRegister=true;
                }
            } else zeroRegister=false;


            ArmUtil.horizontalSetPower(Math.pow(gamepad1.left_stick_x, 3)*0.1); // Arm movement
            ArmUtil.verticalSetPower(Math.pow(-gamepad1.left_stick_y, 5)*0.5);


            if(gamepad1.right_stick_x!=0||gamepad1.right_stick_y!=0||gamepad1.right_stick_button) { // Reset stabilization
                lastHPos=ArmUtil.hPos;
                lastVPos=ArmUtil.vPos;
                stabilize=true;
            }
            if(ArmUtil.hWrist((int)userHWrist-(stabilize?ArmUtil.hPos-lastHPos:0))) stabilize=false;
            if(gamepad1.right_stick_button) userHWrist=0;


            ArmUtil.winchSetPower(Math.pow(-gamepad1.right_stick_y, 5)*0.25);



            if(gamepad1.left_bumper) crush=1; // Buttons make grabber stay closed or open
            if(gamepad1.right_bumper) crush=-1;
            if(gamepad1.left_trigger+gamepad1.right_trigger>0) crush=0;

            if(crush==1) ArmUtil.grab(1);
            else if(crush==-1) ArmUtil.grab(-1);
            else ArmUtil.grab(gamepad1.left_trigger-gamepad1.right_trigger);


            // Chassis
            bl.setPower(-Math.pow(gamepad2.left_trigger*boolToInt(gamepad2.left_bumper), 3));
            fl.setPower(-Math.pow(gamepad2.left_trigger*boolToInt(gamepad2.left_bumper), 3));

            br.setPower(Math.pow(gamepad2.right_trigger*boolToInt(gamepad2.right_bumper), 3));
            fr.setPower(Math.pow(gamepad2.right_trigger*boolToInt(gamepad2.right_bumper), 3));

            telemetry.update();
        }
        ArmUtil.stop();
    }
}
