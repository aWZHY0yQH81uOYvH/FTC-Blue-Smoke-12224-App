package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ChassisUtil {
    //motors
    public static DcMotor fl, fr, bl, br;

    private static double lastLPower=0, lastRPower=0;

    public static void getHardware(HardwareMap hardwareMap) {
        bl=hardwareMap.get(DcMotor.class, "bl");
        br=hardwareMap.get(DcMotor.class, "br");
        fl=hardwareMap.get(DcMotor.class, "fl");
        fr=hardwareMap.get(DcMotor.class, "fr");

        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.FORWARD);
    }

    public static void setPower(double power) {
        setPower(power, power); //oh yeah efficiency
    }

    public static void setPower(double left, double right) {
        if(Math.abs(left-lastLPower)>0.005) {
            fl.setPower(left);
            bl.setPower(left);
            lastLPower=left;
        }
        if(Math.abs(right-lastRPower)>0.005) {
            fr.setPower(right);
            br.setPower(right);
            lastRPower=right;
        }
    }

    public static void stop() {
        setPower(0);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }
}
