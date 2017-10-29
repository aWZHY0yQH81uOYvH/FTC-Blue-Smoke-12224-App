package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.*;

import java.util.concurrent.locks.Lock;

@TeleOp
@Disabled
public class i2cTest extends LinearOpMode {

    private static final int port=0;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initializing");
        telemetry.update();

        DeviceInterfaceModule dim=hardwareMap.get(DeviceInterfaceModule.class, "Device Interface Module 1");

        I2cDevice blinkm=hardwareMap.get(I2cDevice.class, "blink");

        byte[] readCache=blinkm.getI2cReadCache();
        Lock readLock=blinkm.getI2cReadCacheLock();
        byte[] writeCache=blinkm.getI2cWriteCache();
        Lock writeLock=blinkm.getI2cWriteCacheLock();

        /*byte data[]={'h','e','l','l','o'};
        blinkm.copyBufferIntoWriteBuffer(data);
        I2cAddr blinkmaddr=I2cAddr.create7bit(8);
        blinkm.enableI2cWriteMode(blinkmaddr,'!',6);
        blinkm.setI2cPortActionFlag();
        blinkm.writeI2cCacheToController();*/

        I2cAddr blinkmaddr=I2cAddr.create7bit(8);
        /*blinkm.enableI2cReadMode(blinkmaddr,0,6);
        blinkm.setI2cPortActionFlag();
        blinkm.writeI2cCacheToController();
        sleep(1000);
        //while(!blinkm.isI2cPortReady()) sleep(10);
        blinkm.readI2cCacheFromController();
        byte data[]=blinkm.getCopyOfReadBuffer();
        String stuff="";
        for(byte part:data) stuff+=(int)part+" | ";
        telemetry.addData("Data", stuff);*/

        readLock.lock();
        blinkm.enableI2cReadMode(blinkmaddr,3,6);
        blinkm.setI2cPortActionFlag();
        blinkm.writeI2cCacheToController();
        readLock.unlock();

        while(!blinkm.isI2cPortReady()) {
            telemetry.addData("Status", "waiting for the port to be ready...");
            telemetry.update();
            sleep(1000);
        }

        blinkm.readI2cCacheFromController();

        /*int count=0;
        String data="";
        readLock.lock();
        while(readCache[1]==0) {
            data="";
            for(int y=0; y<readCache.length; y++) data+=(int)readCache[y]+" | ";
            telemetry.addData("Data", data);
            telemetry.addData("Try", count);
            telemetry.update();
            readLock.unlock();
            blinkm.readI2cCacheFromController();
            readLock.lock();
            count++;
            sleep(1000);
        }*/
        for(int x=0; x<5; x++) {
            String data="";
            readLock.lock();
            for(int y=0; y<readCache.length; y++) data+=(char)readCache[y]+" | ";
            telemetry.addData("Data", data);
            telemetry.addData("Try", x);
            telemetry.update();
            readLock.unlock();
            blinkm.readI2cCacheFromController();
            sleep(1000);
        }

        readLock.lock();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while(opModeIsActive()) {

            telemetry.update();
        }
    }
}