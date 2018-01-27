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

    boolean zeroRegister=false, stabilize=false, stabilizeRegister=false, wristRotation=false, wristRotateRegister=false, armIsMoving=false;
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

        // Al Arm
        Servo alArm=hardwareMap.get(Servo.class, "alArm");
        alArm.setPosition(0.04);

        // Wrist rotation
        Servo wristRotate=hardwareMap.get(Servo.class, "wristRotate");
        wristRotate.setPosition(0.6);

        // Variables
        double lastLPower=0, lastRPower=0;

        ArmUtil.armInit(); // Zero encoders and things

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();





        // ##################################################
        // #                      RUN                       #
        // ##################################################

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                userHWrist=ArmUtil.limitHWrist(userHWrist-gamepad1.right_stick_x);

                //wrist extendo management
                /*
                    U     U  N     N  TTTTTTT  EEEEEEE   SSSSS   TTTTTTT  EEEEEEE  DDDDDD
                    U     U  NN    N     T     E        S     S     T     E        D     D
                    U     U  N N   N     T     E        S           T     E        D     D
                    U     U  N  N  N     T     EEEEEEE   SSSSS      T     EEEEEEE  D     D
                    U     U  N   N N     T     E              S     T     E        D     D
                    U     U  N    NN     T     E        S     S     T     E        D     D
                     UUUUU   N     N     T     EEEEEEE   SSSSS      T     EEEEEEE  DDDDDD
                */
                if (armIsMoving) {
                    ArmUtil.manageWinchExtendo();
                }
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

                    ArmUtil.horizontalToPosition(0, 0.5);

                    zeroRegister=true;
                }
            } else zeroRegister=false;

            if(gamepad1.a) ArmUtil.horizontalSetPower(gamepad1.left_stick_x*0.5);
            else ArmUtil.horizontalSetPower(gamepad1.left_stick_x*0.25); // Arm movement
            ArmUtil.verticalSetPower(-gamepad1.left_stick_y*0.5);

            if(gamepad1.dpad_up) {
                if (!armIsMoving) {
                    ArmUtil.extendoSetPower(1);
                    armIsMoving=true;
                }
            } else if(gamepad1.dpad_down) {
                if (!armIsMoving) {
                    ArmUtil.extendoSetPower(1);
                    armIsMoving = true;
                }
            } else {
                ArmUtil.extendoSetPower(0);
                ArmUtil.winchSetPower(-gamepad1.right_stick_y*0.3); // Move wrist only if arm isn't moving
                if (armIsMoving) armIsMoving=false;
            }


            //times[2]=runTime.nanoseconds();





            // ##################################################
            // #                     WRIST                      #
            // ##################################################

            ArmUtil.updateWinchPos();
            // press b to stabilize wrist horizontal angle
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

            double x=gamepad2.right_stick_x+0.2*gamepad2.left_stick_x, y=gamepad2.right_stick_y+0.2*gamepad2.left_stick_y;
            double leftPower=gamepad2.left_trigger*boolToInt(gamepad2.left_bumper)*0.2, rightPower=gamepad2.right_trigger*boolToInt(gamepad2.right_bumper)*0.2;
            if(gamepad2.a) {
                y=0;
                leftPower=rightPower;
            }
            double left=ArmUtil.limit(-y+x+leftPower, -1, 1)*0.75, right=ArmUtil.limit(-y-x+rightPower, -1, 1)*0.75;
            if(Math.abs(lastLPower-left)>0.005) {
                bl.setPower(-left);
                fl.setPower(-left);
                lastLPower=left;
            }
            if(Math.abs(lastRPower-right)>0.005) {
                br.setPower(right);
                fr.setPower(right);
                lastRPower=right;
            }

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
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        timer.cancel();
    }
}
