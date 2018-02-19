package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(group="NotFakeDrive")
public class NotFakeAutonomousReverse extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        // ##################################################
        // #                      INIT                      #
        // ##################################################

        telemetry.addData("Status", "Initializing");
        telemetry.update();

        ArmUtil.getHardware(hardwareMap);
        ChassisUtil.getHardware(hardwareMap);
        AutonomousStuff.getHardware(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // ##################################################
        // #                      RUN                       #
        // ##################################################

        ArmUtil.armInit();
        ArmUtil.horizontalToPosition(0, 0.03);
        AutonomousStuff.autonomousInit();

        String jewelResult=AutonomousStuff.jewel();
        telemetry.addData("Jewel result", jewelResult);

        if(!jewelResult.contains("timeout")) {
            telemetry.addData("Safe zone result",
                    AutonomousStuff.safeZone(AutonomousStuff.REVERSE));
            telemetry.update();
        }

        AutonomousStuff.lowerArm();

        while(opModeIsActive()) {}

        ArmUtil.stop();
        ChassisUtil.stop();
    }
}
