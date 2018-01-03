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

    // ##################################################
    // #           CONSTANTS/GLOBAL VARIABLES           #
    // ##################################################

    // Motors and sensors
    public static DcMotor horizontalTurret, verticalTurret, wristWinch;
    public static ModernRoboticsTouchSensor horizontalLimit, verticalLimit, wristLimit, grabberLimit;
    public static Servo wristHorizontal;
    public static CRServo grabber;

    private static int horizontalOffset=0, verticalOffset=0, winchOffset=0; // Encoder offsets

    // Powers and positions of all motors
    public static double hPower=0, vPower=0, winchPower=0;
    public static int hPos=0, vPos=0, winchPos=0;

    // Constants
    private static final int VERTICAL_INIT=45, VERTICAL_MIN_OB=-10, VERTICAL_MIN=-45, VERTICAL_MAX=45; // Degrees
    private static final int HORIZONTAL_MIN=-180, HORIZONTAL_MAX=90;
    private static final int VERTICAL_STEP=28, HORIZONTAL_STEP=12; // Conversion from steps to degrees
    private static final int H_WRIST_RANGE=80, H_WRIST_OFFSET=0;
    private static final int WRIST_INIT=600, WRIST_MIN=-1500, WRIST_MAX=900; // Steps





    // ##################################################
    // #                   UTILITIES                    #
    // ##################################################

    private static void sleep(int millis) { // Nice delay function
        try {
            Thread.sleep(millis);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    // TODO setPram never used because it's weird?
    private static void setPram(Object thing, Object pram, Object lastPram) {
        if(!pram.equals(lastPram)&&thing instanceof DcMotor) {
            if(pram instanceof DcMotor.RunMode) ((DcMotor)thing).setMode((DcMotor.RunMode)pram);
            else if(pram instanceof Double) ((DcMotor)thing).setPower((double)pram);
            else if(pram instanceof Integer) ((DcMotor)thing).setTargetPosition((int)pram);
        }
    }

    public static int limit(int in, int min, int max) { // Limits range of other stuff
        if(in<min) return min;
        else if(in>max) return max;
        else return in;
    }

    public static double limit(double in, double min, double max) { // Limits range of other stuff
        if(in<min) return min;
        else if(in>max) return max;
        else return in;
    }

    public static void armInit() {
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
        //horizontalTurret.setMotorType(MotorConfigurationType.getMotorType(TetrixMotor.class)); // Need to use custom PID values (changed D to 250)
        verticalTurret.setMotorType(MotorConfigurationType.getMotorType(NeveRest60Gearmotor.class));
        wristWinch.setMotorType(MotorConfigurationType.getMotorType(TetrixMotor.class));

        wristHorizontal.setPosition(0.5); // Center wrist servo

        verticalOffset=verticalTurret.getCurrentPosition()+VERTICAL_INIT*VERTICAL_STEP; // Get zeros for arm
        horizontalOffset=horizontalTurret.getCurrentPosition();

        verticalToPosition(0, 1); // Bring arm up

        wristWinch.setPower(1); // Pull winch up quickly
        for(int x=0; x<100&&!wristLimit.isPressed(); x++) sleep(10);
        wristWinch.setPower(-0.25); // Release slowly until it drops off the switch
        for(int x=0; x<100&&wristLimit.isPressed(); x++) sleep(10);
        wristWinch.setPower(0);
        winchOffset=wristWinch.getCurrentPosition()+WRIST_INIT; // Capture 0
    }

    public static void stop() { // Stops movement of everything
        verticalTurret.setPower(0);
        horizontalTurret.setPower(0);
        wristWinch.setPower(0);
        verticalTurret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        horizontalTurret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        wristWinch.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }





    // ##################################################
    // #                 VERTICAL MOTOR                 #
    // ##################################################

    private static boolean vLocked=false;
    private static double vLastPow=0;

    public static void verticalToPosition(int deg, double power) { // Vertical to set angle
        verticalTurret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        verticalTurret.setPower(power);
        updateHPos();
        deg=limit(deg, (hPos==0||hPos>=120)?VERTICAL_MIN:VERTICAL_MIN_OB, VERTICAL_MAX);
        vPos=deg;
        deg=deg*VERTICAL_STEP+verticalOffset;
        verticalTurret.setTargetPosition(deg);
        for(int x=0; x<500&&Math.abs(verticalTurret.getCurrentPosition()-deg)>=50; x++) sleep(10); // Custom loose tolerance
    }

    public static void verticalSetPower(double power) { // Vertical constant speed
        // Assuming hPos and vPos have already been updated

        int min=(hPos==0||Math.abs(hPos)>=90)?VERTICAL_MIN:VERTICAL_MIN_OB; // Current minimum based on arm position

        if((vPos<min&&power>0)||(vPos<=VERTICAL_MAX&&vPos>=min)||(vPos>VERTICAL_MAX&&power<0)) vPower=power; // Allow arm to be brought back from extremes
        else if(vPos<min&&Math.abs(hPower)>0) vPower=0.25; // Automatically move arm up if it's about to hit the robot
        else vPower=0;

        if(Math.abs(vPower)<0.005) { // Lock motor if power is low
            if(!vLocked) { // Only send commands once
                verticalTurret.setTargetPosition(verticalTurret.getCurrentPosition());
                verticalTurret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                verticalTurret.setPower(0.1);
                vLocked=true;
            }
        } else {
            if(vLocked) {
                verticalTurret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                vLocked=false;
            }
            if(Math.abs(vLastPow-vPower)>0.005) { // Only update speed if there's a difference
                verticalTurret.setPower(vPower);
                vLastPow=vPower;
            }

        }
    }

    public static void updateVPos() { // Get degrees of vertical motor
        vPos=(verticalTurret.getCurrentPosition()-verticalOffset)/VERTICAL_STEP;
    }





    // ##################################################
    // #                HORIZONTAL MOTOR                #
    // ##################################################

    private static boolean hLocked=false;
    private static double hLastPow=0;

    public static void horizontalToPosition(int deg, double power) { // Horizontal to set angle
        horizontalTurret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        horizontalTurret.setPower(power);
        deg=limit(deg, HORIZONTAL_MIN, HORIZONTAL_MAX);
        hPos=deg;
        deg=deg*12+horizontalOffset;
        horizontalTurret.setTargetPosition(deg);
        for(int x=0; x<500&&Math.abs(horizontalTurret.getCurrentPosition()-deg)>=10; x++) sleep(10); // Tighter tolerance on horizontal
    }

    public static void horizontalSetPower(double power) { // Horizontal constant speed
        // Assuming hPos and vPos have already been updated

        if(((hPos<HORIZONTAL_MIN&&power>0)||(hPos<=HORIZONTAL_MAX&&hPos>=HORIZONTAL_MIN)||(hPos>HORIZONTAL_MAX&&power<0))&&(hPos!=0||vPos>=VERTICAL_MIN_OB)) hPower=power; // Allow arm to be brought back from extremes
        else hPower=0;

        if(Math.abs(hPower)<0.0025) { // Lock motor if power is low
            if(!hLocked) { // Only send commands once
                horizontalTurret.setTargetPosition(horizontalTurret.getCurrentPosition());
                horizontalTurret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                horizontalTurret.setPower(0.1);
                hLocked=true;
            }
        } else {
            if(hLocked) {
                horizontalTurret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                hLocked=false;
            }
            if(Math.abs(hLastPow-hPower)>0.0025) { // Only update speed if there's a difference
                horizontalTurret.setPower(hPower);
                hLastPow=hPower;
            }
        }
    }

    public static void updateHPos() { // Get degrees of horizontal motor
        hPos=(horizontalTurret.getCurrentPosition()-horizontalOffset)/HORIZONTAL_STEP;
    }





    // ##################################################
    // #                  WRIST WINCH                   #
    // ##################################################

    private static boolean winchLocked=false;
    private static double winchLastPow=0;

    // TODO Never use winchToPosition?
    public static void winchToPosition(int steps, double power) { // Winch to set angle
        wristWinch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        wristWinch.setPower(power);
        steps=limit(steps, WRIST_MIN, WRIST_MAX);
        winchPos=steps;
        steps+=winchOffset;
        wristWinch.setTargetPosition(steps);
        for(int x=0; x<1000&&Math.abs(wristWinch.getCurrentPosition()-steps)>=50; x++) sleep(10);
    }

    public static void winchSetPower(double power) { // Winch constant power
        // Assuming winchPos has already been updated

        if((winchPos<WRIST_MIN&&power>0)||(winchPos<=WRIST_MAX&&winchPos>=WRIST_MIN)||(winchPos>WRIST_MAX&&power<0)) winchPower=power;
        else winchPower=0;

        if(Math.abs(winchPower)<0.005) { // Lock motor if power is low
            if(!winchLocked) { // Only send commands once
                wristWinch.setTargetPosition(wristWinch.getCurrentPosition());
                wristWinch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                wristWinch.setPower(0.01);
                winchLocked=true;
            }
        } else {
            if(winchLocked) {
                wristWinch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                winchLocked=false;
            }
            if(Math.abs(winchLastPow-winchPower)>0.005) { // Only update speed if there's a difference
                wristWinch.setPower(winchPower);
                winchLastPow=winchPower;
            }
        }
    }

    public static void updateWinchPos() {
        winchPos=wristWinch.getCurrentPosition()-winchOffset;
    }





    // ##################################################
    // #             HORIZONTAL WRIST SERVO             #
    // ##################################################

    private static double lastHWristPos=0;

    public static boolean hWrist(int deg) { // Wrist limiting function
        boolean limiting=false;
        if(Math.abs(deg)>H_WRIST_RANGE) {
            limiting=true;
            if(deg>0) deg=H_WRIST_RANGE;
            else if(deg<0) deg=-H_WRIST_RANGE;
        }
        double pos=(deg+H_WRIST_OFFSET)/160.0+0.5;
        if(Math.abs(lastHWristPos-pos)>0.0025) { // Only update position if there's a difference
            wristHorizontal.setPosition(pos);
            lastHWristPos=pos;
        }
        return limiting;
    }

    public static double limitHWrist(double in) { // Limits range of wrist
        if(in<-H_WRIST_RANGE) return -H_WRIST_RANGE;
        else if(in>H_WRIST_RANGE)return H_WRIST_RANGE;
        else return in;
    }





    // ##################################################
    // #                    GRABBER                     #
    // ##################################################

    private static boolean grabOpening=false; // If grabber is opening
    private static double grabLastPow=0;

    public static void grab(double power) { // Set speed of grabber grabbiness
        double pow=0;
        if(power>0||grabberLimit.isPressed()) { // Allow grab if closing or if grabber still in range
            pow=-power;
            grabOpening=true;
        } else if(grabOpening) { // Allow thing to be opened a little past the switch
            sleep(250);
            grabOpening=false;
        }
        if(Math.abs(grabLastPow-pow)>0.01) { // Only update power if there's a difference
            grabber.setPower(pow);
            grabLastPow=pow;
        }
    }
}
