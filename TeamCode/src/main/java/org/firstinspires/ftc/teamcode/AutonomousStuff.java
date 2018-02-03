package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;

public class AutonomousStuff {

    // ##################################################
    // #           CONSTANTS/GLOBAL VARIABLES           #
    // ##################################################

    public static ColorSensor alSensor, bottomColorSensor;
    public static Servo alArm;
    public static ModernRoboticsI2cGyro gyro;

    private static final double AL_UP=0.04, AL_START_SCAN=0.4, AL_END_SCAN=0.7, AL_SCAN_INCRAMENT=0.01;

    private static final double JEWEL_SPEED=0.25, RETURN_JEWEL_SPEED=0.25;
    private static final int JEWEL_TIME=500;

    private static final double DRIVE_SPEED=0.15;
    private static final int DRIVE_TIMEOUT=3000;

    public static final boolean FORWARD=true, REVERSE=false;





    // ##################################################
    // #                   UTILITIES                    #
    // ##################################################

    public static void getHardware(HardwareMap hardwareMap) {
        alSensor=hardwareMap.get(ColorSensor.class, "alSensor");
        bottomColorSensor=hardwareMap.get(ColorSensor.class, "bottomSensor");
        bottomColorSensor.setI2cAddress(I2cAddr.create8bit(0x42));
        alSensor.enableLed(true);
        bottomColorSensor.enableLed(true);
        alArm=hardwareMap.get(Servo.class, "alArm");
        gyro=hardwareMap.get(ModernRoboticsI2cGyro.class, "gyro");
    }

    public static void autonomousInit() {
        alArm.setPosition(AL_UP);
        gyro.calibrate();
        while(gyro.isCalibrating());
    }

    private static void sleep(int millis) { // Nice delay function
        try {
            Thread.sleep(millis);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }





    // ##################################################
    // #                   ACTIVITIES                   #
    // ##################################################

    public static String jewel() {
        int red=0, blue=0;
        String result;
        alArm.setPosition(AL_START_SCAN);
        sleep(1000);
        for(double x=AL_START_SCAN; x<=AL_END_SCAN; x+=AL_SCAN_INCRAMENT) {
            alArm.setPosition(x);
            sleep(50);
            red+=alSensor.red();
            blue+=alSensor.blue();
        }
        int dir=0;
        if(red==blue) result="Colors are equal; cannot determine";
        else if((red>blue)==(bottomColorSensor.red()>bottomColorSensor.blue())) {
            result="Colors are same; moved clockwise";
            dir=1;
        } else {
            result="Colors are different; moved counterclockwise";
            dir=-1;
        }
        ChassisUtil.setPower(JEWEL_SPEED*dir, -JEWEL_SPEED*dir);
        sleep(JEWEL_TIME);
        ChassisUtil.setPower(0);
        alArm.setPosition(AL_UP);
        sleep(1000);
        ChassisUtil.setPower(-RETURN_JEWEL_SPEED*dir, RETURN_JEWEL_SPEED*dir);
        int timeout=0;
        for(; timeout<1000&&Math.abs(gyro.getHeading())>5; timeout++) sleep(10);
        if(timeout>=1000) result+="\nReturn timeout";
        else result+="\nReturn success";
        ChassisUtil.setPower(0);
        return result;
    }

    public static String safeZone(boolean direction) {
        String result;
        if(direction) ChassisUtil.setPower(DRIVE_SPEED);
        else ChassisUtil.setPower(-DRIVE_SPEED);
        sleep(2250);
        int timeout=0;
        for(; timeout<DRIVE_TIMEOUT/10&&Math.abs(bottomColorSensor.red()-bottomColorSensor.blue())<2; timeout++) sleep(10);
        if(timeout>=DRIVE_TIMEOUT/10) result="Movement timeout";
        else result="Found line in "+timeout*10+"ms";
        ChassisUtil.setPower(0);
        return result;
    }

    public static void lowerArm() {
        ArmUtil.verticalToPosition(30, 0.1);
        ArmUtil.winchToPosition(-500, 0.25);
        ArmUtil.verticalToPosition(-45, 0.1);
    }
}
