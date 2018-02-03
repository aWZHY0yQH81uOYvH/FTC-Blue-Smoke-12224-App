package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(group="NotFakeJewel")
public class NotFakeAutonomousJewel extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        // ##################################################
        // #                      INIT                      #
        // ##################################################

        telemetry.addData("Status", "Initializing");
        telemetry.update();

        ChassisUtil.getHardware(hardwareMap);
        AutonomousStuff.getHardware(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // ##################################################
        // #                      RUN                       #
        // ##################################################

        AutonomousStuff.autonomousInit();

        telemetry.addData("Jewel result",
                AutonomousStuff.jewel());
        telemetry.update();

        while(opModeIsActive()) {}

        ChassisUtil.stop();
    }
}
