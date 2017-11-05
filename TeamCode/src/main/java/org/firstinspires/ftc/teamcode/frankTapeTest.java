package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Frank on 11/5/2017.
 */

@TeleOp
public class frankTapeTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initializing");
        telemetry.update();



        ColorSensor color = hardwareMap.get(ColorSensor.class,"colorbot");

        int currentColorState = 0; //0 = grey, 1 = white, 2 = red, 3 = blue



        telemetry.addData("Stats", "Initialized");
        telemetry.update();

        waitForStart();

        while(opModeIsActive())
        {
            //testing for if it's red

            //if red is almost full
            if(color.red() >= 230)
            {

               // if blue is close to blank
                if(color.blue() <= 20)
                {

                    //if green is close to blank
                    if(color.green() <= 20)
                    {

                        //then it's on red tape
                        currentColorState = 2;

                    }

                }

            }

            //testing for blue

            //if blue is almost full
            else if (color.blue() >= 230)
            {

                //if red is close to blank
                if(color.red() <= 20)
                {

                    //if green is close to blank
                    if(color.green() <= 20)
                    {

                        //then it's on blue tape
                        currentColorState = 3;

                    }

                }

            }

            //testing for white

            else if(color.blue() >= 230)
            {

                if(color.green() >= 230)
                {

                    if(color.red() >= 230)
                    {

                        //then it's on white tape
                        currentColorState = 1;

                    }

                }

            }

            else
            {

                //then it's on the grey mate
                currentColorState = 0;
            }

            //Add data to the telemetry
            telemetry.addData("Current Color Value", currentColorState);
            telemetry.addData("Color Value Key", "0 = grey; 1 = white; 2 = red; 3 = blue");

            telemetry.update();
        }
    }

}
