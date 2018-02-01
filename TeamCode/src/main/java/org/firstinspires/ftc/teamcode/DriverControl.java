package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
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

    boolean zeroRegister=false, stabilize=false, stabilizeRegister=false, wristRotation=false, wristRotateRegister=false;
    int crush=0, lastHPos=0;
    double userHWrist=0;

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
        ArmUtil.lengthSensor=hardwareMap.get(AnalogInput.class, "lengthSensor");

        // Chassis
        ChassisUtil.bl=hardwareMap.get(DcMotor.class, "bl");
        ChassisUtil.br=hardwareMap.get(DcMotor.class, "br");
        ChassisUtil.fl=hardwareMap.get(DcMotor.class, "fl");
        ChassisUtil.fr=hardwareMap.get(DcMotor.class, "fr");


        // Al Arm
        Servo alArm=hardwareMap.get(Servo.class, "alArm");

        // Wrist rotation
        Servo wristRotate=hardwareMap.get(Servo.class, "wristRotate");

        // Variables
        double lastLPower=0, lastRPower=0;

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        alArm.setPosition(0.04);

        wristRotate.setPosition(0.6);

        ArmUtil.armInit(); // Zero encoders and things
        ChassisUtil.init();


        // ##################################################
        // #                      RUN                       #
        // ##################################################

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                userHWrist=ArmUtil.limitHWrist(userHWrist-gamepad1.right_stick_x);
            }
        }, 0, 10); // 10ms

        //ElapsedTime runTime = new ElapsedTime();
        //long times[]=new long[6];

        while(opModeIsActive()) {
            //times[0]=runTime.nanoseconds();
            telemetry.addData("Vertical Pos", ArmUtil.vPos);
            telemetry.addData("Vertical Pow", ArmUtil.vPower);
            telemetry.addData("Horizontal Pos", ArmUtil.hPos);
            telemetry.addData("Horizontal Pow", ArmUtil.hPower);
            telemetry.addData("HWrist Pos", userHWrist);
            telemetry.addData("VWrist Pos", ArmUtil.winchPos);
            telemetry.addData("VWrist Pow", ArmUtil.winchPower);

            //times[1]=runTime.nanoseconds();





            // ##################################################
            // #                      ARM                       #
            // ##################################################

            ArmUtil.updateHPos();
            ArmUtil.updateVPos();
            ArmUtil.updateExtendoPos();
            if(gamepad1.left_stick_button) { // Center with joystick button
                if(!zeroRegister) {
                    //if(ArmUtil.hPos==0) ArmUtil.verticalToPosition(0, 0.5);
                    //else ArmUtil.horizontalToPosition(0, 0.3);

                    ArmUtil.horizontalToPosition(0, 0.1);

                    zeroRegister=true;
                }
            } else zeroRegister=false;

            if(gamepad1.a) ArmUtil.horizontalSetPower(gamepad1.left_stick_x*0.5);
            else ArmUtil.horizontalSetPower(gamepad1.left_stick_x*0.25); // Arm movement

            ArmUtil.verticalSetPower(-gamepad1.left_stick_y*0.5);

            if(gamepad1.dpad_up) {
                ArmUtil.extendoSetPower(1);
                //ArmUtil.maintainWristWinch();
            } else if(gamepad1.dpad_down) {
                ArmUtil.extendoSetPower(-1);
                //ArmUtil.maintainWristWinch();
            } else {
                ArmUtil.extendoSetPower(0);

            }

            ArmUtil.winchSetPower(-gamepad1.right_stick_y*0.3); // Move wrist only if arm isn't moving

            //times[2]=runTime.nanoseconds();





            // ##################################################
            // #                     WRIST                      #
            // ##################################################

            ArmUtil.updateWinchPos();
            if(gamepad1.b) {
                if(!stabilizeRegister) {
                    stabilize=!stabilize;
                    if(!stabilize) {
                        userHWrist+=ArmUtil.hPos-lastHPos;
                        lastHPos=ArmUtil.hPos;
                    }
                    stabilizeRegister=true;
                }
            } else stabilizeRegister=false;
            if(gamepad1.right_stick_x!=0||gamepad1.right_stick_y!=0||gamepad1.right_stick_button) { // Reset stabilization
                lastHPos=ArmUtil.hPos;
            }
            if(ArmUtil.hWrist(-(int)userHWrist-(stabilize?ArmUtil.hPos-lastHPos:0))) {
                lastHPos=ArmUtil.hPos;
                stabilize=false;
            }
            if(gamepad1.right_stick_button) userHWrist=0;

            if(gamepad1.x) {
                if(!wristRotateRegister) {
                    wristRotation=!wristRotation;
                    if(wristRotation) wristRotate.setPosition(0);
                    else wristRotate.setPosition(0.6);
                    wristRotateRegister=true;
                }
            } else wristRotateRegister=false;

            //times[3]=runTime.nanoseconds();





            // ##################################################
            // #                    GRABBER                     #
            // ##################################################

            if(gamepad1.left_bumper) crush=1; // Buttons make grabber stay closed or open
            if(gamepad1.right_bumper) crush=-1;
            if(gamepad1.left_trigger+gamepad1.right_trigger>0) crush=0;

            if(crush==1) ArmUtil.grab(1);
            else if(crush==-1) ArmUtil.grab(-1);
            else ArmUtil.grab(gamepad1.left_trigger-gamepad1.right_trigger);

            //times[4]=runTime.nanoseconds();





            // ##################################################
            // #                    CHASSIS                     #
            // ##################################################

            double x=gamepad2.right_stick_x+0.4*gamepad2.left_stick_x, y=gamepad2.right_stick_y+0.2*gamepad2.left_stick_y;
            double leftPower=gamepad2.left_trigger*boolToInt(gamepad2.left_bumper)*0.2, rightPower=gamepad2.right_trigger*boolToInt(gamepad2.right_bumper)*0.2;
            if(gamepad2.a) {
                y=0;
                leftPower=rightPower;
            }
            double left=ArmUtil.limit(-y+x+leftPower, -1, 1)*0.75, right=ArmUtil.limit(-y-x+rightPower, -1, 1)*0.75;

            ChassisUtil.setPower(left, right);


            // Old chassis control
            //double leftPower=gamepad2.left_trigger;
            //double rightPower=gamepad2.right_trigger;

            //if(Math.abs(leftPower-rightPower)<=0.2) {
            //    leftPower+=rightPower;
            //    leftPower/=2;
            //    rightPower=leftPower;
            //}

            //bl.setPower(-Math.pow(leftPower, 2)*boolToInt(gamepad2.left_bumper));
            //fl.setPower(-Math.pow(leftPower, 2)*boolToInt(gamepad2.left_bumper));
            //br.setPower(Math.pow(rightPower, 2)*boolToInt(gamepad2.right_bumper));
            //fr.setPower(Math.pow(rightPower, 2)*boolToInt(gamepad2.right_bumper));

            //times[5]=runTime.nanoseconds();

            //telemetry.addData("Telemetry Update/Loop", times[0]);
            //telemetry.addData("Add Data", times[1]);
            //telemetry.addData("Arm", times[2]);
            //telemetry.addData("Wrist", times[3]);
            //telemetry.addData("Grabber", times[4]);
            //telemetry.addData("Chassis", times[5]);

            //runTime.reset();
            //ArmUtil.winchSetPower(0);
            //telemetry.addData("winchSetPower", runTime.nanoseconds());

            telemetry.update();
        }
        ArmUtil.stop();
        ChassisUtil.stop();
        timer.cancel();
    }
}
