package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

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

        // Setup classes
        ArmUtil.getHardware(hardwareMap);
        ChassisUtil.getHardware(hardwareMap);
        AutonomousStuff.getHardware(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        ArmUtil.armInit();
        AutonomousStuff.autonomousInit();


        // ##################################################
        // #                      RUN                       #
        // ##################################################

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                userHWrist=ArmUtil.limitHWrist(userHWrist-gamepad1.right_stick_x);
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





            // ##################################################
            // #                      ARM                       #
            // ##################################################

            ArmUtil.updateHPos();
            ArmUtil.updateVPos();
            ArmUtil.updateExtendoPos();
            if(gamepad1.left_stick_button) { // Center with joystick button
                if(!zeroRegister) {
                    ArmUtil.horizontalToPosition(0, 0.03);
                    zeroRegister=true;
                }
            } else zeroRegister=false;

            if(gamepad1.a) ArmUtil.horizontalSetPower(gamepad1.left_stick_x*0.5);
            else ArmUtil.horizontalSetPower(gamepad1.left_stick_x*0.1); // Arm movement

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
            // TODO make this move with the arm
            ArmUtil.winchSetPower(-gamepad1.right_stick_y*0.3);





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
                    if(wristRotation) ArmUtil.wristRotate(ArmUtil.ROTATE_VERTICAL);
                    else ArmUtil.wristRotate(ArmUtil.ROTATE_FLAT);
                    wristRotateRegister=true;
                }
            } else wristRotateRegister=false;





            // ##################################################
            // #                    GRABBER                     #
            // ##################################################

            if(gamepad1.left_bumper) crush=1; // Buttons make grabber stay closed or open
            if(gamepad1.right_bumper) crush=-1;
            if(gamepad1.left_trigger+gamepad1.right_trigger>0) crush=0;

            if(crush==1) ArmUtil.grab(1);
            else if(crush==-1) ArmUtil.grab(-1);
            else ArmUtil.grab(gamepad1.left_trigger-gamepad1.right_trigger);





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

            //telemetry.addData("Telemetry Update/Loop", times[0]);
            //telemetry.addData("Add Data", times[1]);
            //telemetry.addData("Arm", times[2]);
            //telemetry.addData("Wrist", times[3]);
            //telemetry.addData("Grabber", times[4]);
            //telemetry.addData("Chassis", times[5]);


            telemetry.update();
        }
        ArmUtil.stop();
        ChassisUtil.stop();
        timer.cancel();
    }
}
