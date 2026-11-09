package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Indicator Light Test", group = "Test")
public class Indicator extends LinearOpMode {

    private Servo indicatorLight;

    @Override
    public void runOpMode() {
        // Map the servo from the hardware map (name must match your config file)
        indicatorLight = hardwareMap.get(Servo.class, "indicatorLight");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Example: Change color/mode based on gamepad buttons
            if (gamepad1.a) {
                indicatorLight.setPosition(0.0); // e.g., Red or Color 1
            } else if (gamepad1.b) {
                indicatorLight.setPosition(0.5); // e.g., Green or Color 2
            } else if (gamepad1.x) {
                indicatorLight.setPosition(1.0); // e.g., Blue or Color 3
            } else {
                // Default or idle state
                indicatorLight.setPosition(0.25);
            }

            telemetry.addData("Light Position", indicatorLight.getPosition());
            telemetry.update();
        }
    }
}

