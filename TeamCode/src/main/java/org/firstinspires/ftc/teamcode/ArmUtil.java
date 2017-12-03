package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsTouchSensor;
import com.qualcomm.hardware.motors.NeveRest60Gearmotor;
import com.qualcomm.hardware.motors.TetrixMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.MotorConfigurationType;

public class ArmUtil {
    public static DcMotor horizontalTurret, verticalTurret, wristWinch;
    public static ModernRoboticsTouchSensor horizontalLimit, verticalLimit, wristLimit, grabberLimit;
    public static Servo wristHorizontal;
    public static CRServo grabber;

    private static int horizontalOffset=0, verticalOffset=0, winchOffset=0;
    private static boolean grabOpening=false;

    public static double hPower=0, vPower=0, winchPower=0;
    public static int hPos=0, vPos=0, winchPos=0;

    private static final int VERTICAL_INIT=-180, VERTICAL_MIN_OB=-12, VERTICAL_MIN=-45, VERTICAL_MAX=45;
    private static final int HORIZONTAL_INIT=6, HORIZONTAL_MIN=-180, HORIZONTAL_MAX=90;
    private static final int H_WRIST_RANGE=80, H_WRIST_OFFSET=0;
    private static final int WRIST_INIT=550, WRIST_MIN=-1337, WRIST_MAX=800, WRIST_STOW=-2800;

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static double limitHWrist(double in) {
        if(in<-H_WRIST_RANGE) return -H_WRIST_RANGE;
        else if(in>H_WRIST_RANGE)return H_WRIST_RANGE;
        else return in;
    }

    public static int limit(int in, int min, int max) {
        if(in<min) return min;
        else if(in>max )return max;
        else return in;
    }

    public static boolean armInit() {
        // Braking
        horizontalTurret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        verticalTurret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        wristWinch.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // Run at constant speed
        horizontalTurret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        verticalTurret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        wristWinch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // Make directions logical
        horizontalTurret.setDirection(DcMotorSimple.Direction.REVERSE);
        verticalTurret.setDirection(DcMotorSimple.Direction.REVERSE);
        wristWinch.setDirection(DcMotorSimple.Direction.FORWARD);
        grabber.setDirection(CRServo.Direction.FORWARD);
        // Ensure encoders are scaled correctly
        horizontalTurret.setMotorType(MotorConfigurationType.getMotorType(TetrixMotor.class));
        verticalTurret.setMotorType(MotorConfigurationType.getMotorType(NeveRest60Gearmotor.class));
        wristWinch.setMotorType(MotorConfigurationType.getMotorType(TetrixMotor.class));

        // Center wrist
        wristHorizontal.setPosition(0.5+H_WRIST_OFFSET);

        // Drop wrist
        wristWinch.setPower(-0.25);
        sleep(500);
        wristWinch.setPower(0);

        // Vertical first
        if(verticalLimit.isPressed()) return false;
        else {
            verticalTurret.setPower(0.2);
            for(int x=0; x<500&&!verticalLimit.isPressed(); x++) sleep(10);
            if(!verticalLimit.isPressed()) return false;
            verticalTurret.setPower(0);
        }
        verticalOffset=verticalTurret.getCurrentPosition()+VERTICAL_INIT;
        verticalToPosition(0, 0.5);
        
        
        // Horizontal
        //if(horizontalLimit.isPressed()) return false;
        //else {
            horizontalTurret.setPower(0.1);
            for(int x=0; x<500&&!horizontalLimit.isPressed(); x++) sleep(10);
            if(!horizontalLimit.isPressed()) return false;
            horizontalTurret.setPower(0);
        //}
        horizontalOffset=horizontalTurret.getCurrentPosition()+HORIZONTAL_INIT;
        horizontalToPosition(0, 0.5);


        // Vertical Wrist
        if(wristLimit.isPressed()) return false;
        else {
            wristWinch.setPower(0.1);
            for(int x=0; x<1000&&!wristLimit.isPressed(); x++) sleep(10);
            if(!wristLimit.isPressed()) return false;
            wristWinch.setPower(0);
        }
        winchOffset=wristWinch.getCurrentPosition()+WRIST_INIT;
        winchToPosition(0, 0.25);

        return true;

    }

    public static double verticalToPosition(double deg, double power) {
        verticalTurret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        verticalTurret.setPower(power);
        int intDeg=(int)deg;
        int actualHPos=(horizontalTurret.getCurrentPosition()-horizontalOffset)/12;
        intDeg=limit(intDeg, (actualHPos==0||actualHPos>=120)?VERTICAL_MIN:VERTICAL_MIN_OB, VERTICAL_MAX);
        vPos=intDeg;
        verticalTurret.setTargetPosition(intDeg*28+verticalOffset);
        for(int x=0; x<500&&verticalTurret.isBusy(); x++) sleep(10);
        return deg;
    }

    public static double horizontalToPosition(double deg, double power) {
        horizontalTurret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        horizontalTurret.setPower(power);
        int intDeg=(int)deg;
        intDeg=limit(intDeg, HORIZONTAL_MIN, HORIZONTAL_MAX);
        hPos=intDeg;
        horizontalTurret.setTargetPosition(intDeg*12+horizontalOffset);
        for(int x=0; x<500&&horizontalTurret.isBusy(); x++) sleep(10);
        return deg;
    }

    public static double winchToPosition(double steps, double power) {
        wristWinch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        wristWinch.setPower(power);
        int intSteps=(int)steps;
        intSteps=limit(intSteps, WRIST_MIN, WRIST_MAX);
        winchPos=intSteps;
        wristWinch.setTargetPosition(intSteps+winchOffset);
        for(int x=0; x<1000&&wristWinch.isBusy(); x++) sleep(10);
        return steps;
    }

    public static void verticalSetPower(double power) {
        hPos=(horizontalTurret.getCurrentPosition()-horizontalOffset)/12;
        vPos=(verticalTurret.getCurrentPosition()-verticalOffset)/28;
        int min=(hPos==0||Math.abs(hPos)>=90)?VERTICAL_MIN:VERTICAL_MIN_OB;
        //if((vPos<min&&power>0)||(vPos<=VERTICAL_MAX&&vPos>=min)||(vPos>VERTICAL_MAX&&power<0)) vPower=power;
        //else if(vPos<min&&Math.abs(hPower)>0) vPower=0.25;
        //else vPower=0;

        vPower=power; // DANGER

        if(Math.abs(vPower)<0.05) {
            verticalTurret.setTargetPosition(verticalTurret.getCurrentPosition());
            verticalTurret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            verticalTurret.setPower(0.1);
        } else {
            verticalTurret.setPower(vPower);
            verticalTurret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public static void horizontalSetPower(double power) {
        hPos=(horizontalTurret.getCurrentPosition()-horizontalOffset)/12;
        if((hPos<HORIZONTAL_MIN&&power>0)||(hPos<=HORIZONTAL_MAX&&hPos>=HORIZONTAL_MIN)||(hPos>HORIZONTAL_MAX&&power<0)) hPower=power;
        else hPower=0;
        if(Math.abs(hPower)<0.05) {
            horizontalTurret.setTargetPosition(horizontalTurret.getCurrentPosition());
            horizontalTurret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            horizontalTurret.setPower(0.1);
        } else {
            horizontalTurret.setPower(hPower);
            horizontalTurret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public static void winchSetPower(double power) {
        winchPos=wristWinch.getCurrentPosition()-winchOffset;
        if((winchPos<WRIST_MIN&&power>0)||(winchPos<=WRIST_MAX&&winchPos>=WRIST_MIN)||(winchPos>WRIST_MAX&&power<0)) winchPower=power;
        else winchPower=0;
        if(Math.abs(winchPower)<0.05) {
            wristWinch.setTargetPosition(wristWinch.getCurrentPosition());
            wristWinch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            wristWinch.setPower(0.1);
        } else {
            wristWinch.setPower(winchPower);
            wristWinch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public static void stop() {
        verticalTurret.setPower(0);
        horizontalTurret.setPower(0);
        wristWinch.setPower(0);
    }

    public static void grab(double power) {
        if(grabberLimit.isPressed()||power>0) {
            grabber.setPower(-power);
            grabOpening=true;
        } else if(grabOpening) {
            sleep(250);
            grabOpening=false;
        } else grabber.setPower(0);
    }

    public static boolean hWrist(int deg) {
        boolean limiting=false;
        if(Math.abs(deg)>H_WRIST_RANGE) {
            limiting=true;
            if(deg>0) deg=H_WRIST_RANGE;
            else if(deg<0) deg=-H_WRIST_RANGE;
        }
        wristHorizontal.setPosition((deg+H_WRIST_OFFSET)/90.0+0.5);
        return limiting;
    }

    public static void stow() {
        verticalToPosition(0, 0.25);
        horizontalToPosition(0, 0.1);
        hWrist(0);
        grab(1);
        sleep(2000);
        grab(0);
        wristWinch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        wristWinch.setPower(1);
        wristWinch.setTargetPosition(WRIST_STOW+winchOffset);
        for(int x=0; x<500&&wristWinch.isBusy(); x++) sleep(10);
        boolean run=true;
        int pressCount=0;
        while(run) {
            if(wristLimit.isPressed()) {
                int x=0;
                for(; x<20&&wristLimit.isPressed(); x++) {
                    verticalSetPower(-0.1);
                    sleep(10);
                }
                if(x<15) pressCount++;
            } else verticalSetPower(0);
            if(pressCount>=3) run=false;
        }
    }
}
