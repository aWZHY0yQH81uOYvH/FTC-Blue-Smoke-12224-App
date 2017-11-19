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

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while(opModeIsActive()) {
            bl.setPower(-gamepad1.left_trigger*boolToInt(gamepad1.left_bumper));
            fl.setPower(-gamepad1.left_trigger*boolToInt(gamepad1.left_bumper));
            br.setPower(gamepad1.right_trigger*boolToInt(gamepad1.right_bumper));
            fr.setPower(gamepad1.right_trigger*boolToInt(gamepad1.right_bumper));

            telemetry.update();
        }
    }
}
