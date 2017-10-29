package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;


@TeleOp
public class ColorTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        //this is for the color sensor

        ColorSensor color;
        color = hardwareMap.colorSensor.get("color");
        color.enableLed(true);

        //this is for the motor

        DcMotor motor;
        motor = hardwareMap.get(DcMotor.class, "motorr");
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while(opModeIsActive()) {
            telemetry.addData("Redness", color.red());
            telemetry.addData("Greeness", color.green());
            telemetry.addData("Blueness", color.blue());
            if(color.red()>1&&color.green()<color.red()/2&&color.blue()<color.red()/2) motor.setPower(0.25);
            else motor.setPower(0);

            telemetry.update();
        }
    }
}
