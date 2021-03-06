package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/*

This uses the back camera on the robot controller to determine which VuMark thing
it's looking at (autonomous).

 */

@TeleOp
@Disabled
public class VuforiaTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initializing Vuforia");
        telemetry.update();

        // Init Vuforia
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        // Special key we had to make
        parameters.vuforiaLicenseKey = "AZ2wdn//////AAAAGd+igk3mFkjUu//Hqbk31B1Dz4LHSIUWiWcEduepObWceIiDsR9tP4YqeWjdMXZLEXOC9SdNO8xaYhy+gbC15PloamZaO0NFX+DCrplKuffSK0V4gJeUIuLt58UoTBL3OpZcYfna11vh8Xl3Rw6uaSl6rjWqy+MxPHJjgQPoNu7hhb7bK7vvfB9IUDk61aKwpZv20372DMWVagzzYXX6Z5jwArqAFxxfYFROxnCU4jBVjD/ZqYhBK+1TD+qaOSaNuAHp6Yqx54I6xr5Zq76joKn9DF1XmSd4u4rfRg+iRk1995ldmWz/lVTH9V/JFecwKOSyYb9iZKcu77/Cjl5wYfhMfV/h419i6b5bZv+KOKKm";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        // Load things into Vuforia
        VuforiaTrackables relicTrackables = vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        relicTrackables.activate();

        while(opModeIsActive()) {
            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
            if (vuMark != RelicRecoveryVuMark.UNKNOWN) telemetry.addData("VuMark", "%s visible", vuMark);
            telemetry.update();
        }
    }
}
