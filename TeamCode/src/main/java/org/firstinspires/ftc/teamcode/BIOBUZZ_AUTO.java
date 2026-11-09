package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name="BIOBUZZ AUTO", group = "Robot")
public class BIOBUZZ_AUTO extends LinearOpMode {
    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;

    DcMotor backRightDrive;
    DcMotor intakeMotor;
    DcMotor transferMotor;
    DcMotorEx shooterMotor;

    Servo flowerServo;
    NormalizedColorSensor colorSensor;
    double intakeSpeed = 0.7;
    double servoPos = 0.19;

    // This assumes a 6000 RPM motor with 28 ticks per revolution, change these if yours is different
    static final double SHOOTER_MAX_RPM = 6000;
    static final double SHOOTER_TICKS_PER_REV = 28;

    ShooterPID shooterPID = new ShooterPID();

    double shooterTargetRPM = 2400;
    double shooterRPM = 0;
    double shooterPower = 0;
    String colorDetected;

    IMU imu;

    @Override
    public void runOpMode() {
        // Sends telemetry to both the Driver Station and FTC Dashboard
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        frontLeftDrive = hardwareMap.get(DcMotor.class, "Front Left Drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "Front Right Drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "Back Left Drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "Back Right Drive");
        intakeMotor = hardwareMap.get(DcMotor.class, "Intake Motor");
        transferMotor = hardwareMap.get(DcMotor.class, "Transfer");
        shooterMotor = hardwareMap.get(DcMotorEx.class,"Shooter");
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorSensor");

        flowerServo = hardwareMap.get(Servo.class, "flower"); // port 5 expansion hub


        // We set the left motors in reverse which is needed for drive trains where the left
        // motors are opposite to the right ones.
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        transferMotor.setDirection(DcMotor.Direction.REVERSE);

        // ShooterPID sets the power itself, so the hub's built in velocity control stays off.
        // The encoder can still be read in this mode.
        shooterMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        imu = hardwareMap.get(IMU.class, "imu");
        // This needs to be changed to match the orientation on your robot
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.UP;

        RevHubOrientationOnRobot orientationOnRobot = new
                RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        flowerServo.setPosition(servoPos);
    }

}
}
