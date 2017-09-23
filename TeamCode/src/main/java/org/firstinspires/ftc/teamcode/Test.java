package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;

@TeleOp
public class Test extends OpMode {
    @Override
    public void init() {
        telemetry.addData("Status", "potato");
        System.out.println("init");
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        System.out.println("start");
    }

    @Override
    public void loop() {
    }
}
