package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;

/*

Just a quick thing to make the wheels go based on the triggers on the controller

 */

@TeleOp
public class ChassisTest extends LinearOpMode {

    private static int boolToInt(boolean in) {
        if(in) return -1;
        else return 1;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        DcMotor bl, br, fl, fr;
        bl = hardwareMap.get(DcMotor.class, "bl");
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br = hardwareMap.get(DcMotor.class, "br");
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fl = hardwareMap.get(DcMotor.class, "fl");
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fr = hardwareMap.get(DcMotor.class, "fr");
        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        boolean isLeftReverse = false;
        boolean isRightReverse = false;

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while(opModeIsActive()) {
            //bl.setPower(-gamepad1.left_trigger*boolToInt(gamepad1.left_bumper));
            //fl.setPower(-gamepad1.left_trigger*boolToInt(gamepad1.left_bumper));
            //br.setPower(gamepad1.right_trigger*boolToInt(gamepad1.right_bumper));
            //fr.setPower(gamepad1.right_trigger*boolToInt(gamepad1.right_bumper));

            //driving

            if(gamepad1.left_trigger > 0.2)
            {
                if(isLeftReverse)
                {

                    bl.setPower(-(gamepad1.left_trigger) * (gamepad1.left_trigger));
                    fl.setPower(-(gamepad1.left_trigger) * (gamepad1.left_trigger));

                }

                else
                {

                    bl.setPower((gamepad1.left_trigger) * (gamepad1.left_trigger));
                    fl.setPower((gamepad1.left_trigger) * (gamepad1.left_trigger));

                }

            }

            else
            {
                bl.setPower(0.0);
                fl.setPower(0.0);
            }

            if(gamepad1.right_trigger > 0.2)
            {
                if(isRightReverse)
                {
                    br.setPower((-gamepad1.right_trigger) * (gamepad1.right_trigger));
                    fr.setPower((-gamepad1.right_trigger) * (gamepad1.right_trigger));
                }

                else
                {

                    br.setPower((gamepad1.right_trigger) * (gamepad1.right_trigger));
                    fr.setPower((gamepad1.right_trigger) * (gamepad1.left_trigger));

                }
            }

            else
            {
                br.setPower(0.0);
                fr.setPower(0.0);
            }

            if(gamepad1.left_bumper)
            {

                isLeftReverse = true;

            }

            else
            {

                isLeftReverse = false;

            }

            if(gamepad1.right_bumper)
            {

                isRightReverse = true;

            }

            else
            {

                isRightReverse = false;

            }


            telemetry.update();
        }
    }
}