package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

@TeleOp
public class Test extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initializing Vuforia");
        telemetry.update();
        VuforiaLocalizer.Parameters frontCameraParams = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        frontCameraParams.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        frontCameraParams.vuforiaLicenseKey = "AZ2wdn//////AAAAGd+igk3mFkjUu//Hqbk31B1Dz4LHSIUWiWcEduepObWceIiDsR9tP4YqeWjdMXZLEXOC9SdNO8xaYhy+gbC15PloamZaO0NFX+DCrplKuffSK0V4gJeUIuLt58UoTBL3OpZcYfna11vh8Xl3Rw6uaSl6rjWqy+MxPHJjgQPoNu7hhb7bK7vvfB9IUDk61aKwpZv20372DMWVagzzYXX6Z5jwArqAFxxfYFROxnCU4jBVjD/ZqYhBK+1TD+qaOSaNuAHp6Yqx54I6xr5Zq76joKn9DF1XmSd4u4rfRg+iRk1995ldmWz/lVTH9V/JFecwKOSyYb9iZKcu77/Cjl5wYfhMfV/h419i6b5bZv+KOKKm";
        frontCameraParams.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;

        VuforiaLocalizer frontCamera = ClassFactory.createVuforiaLocalizer(frontCameraParams);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 3);

        VuforiaTrackables patterns = frontCamera.loadTrackablesFromFile("./FtcRobotController/src/main/assets/FTC_2017-18.xml");

        telemetry.addData("Stats", "Initialized");
        telemetry.update();

        waitForStart();

        patterns.activate();

        while(opModeIsActive()) {
            for(VuforiaTrackable thing : patterns) {
                OpenGLMatrix place = ((VuforiaTrackableDefaultListener) thing.getListener()).getPose();
                if(place==null) telemetry.addData(thing.getName(), "No");
                else telemetry.addData(thing.getName(), "Yes");
            }
            telemetry.update();
        }
    }
}
