package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "BIOBUZZ TELEOP", group = "Robot")

public class basicTeleOp extends OpMode {
    // This declares the four motors needed
    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;
    DcMotor intakeMotor;
    DcMotor transferMotor;
    DcMotor shooterMotor;

    Servo flowerServo;
    double intakeSpeed = 0.7;
    double servoPos = 0.19;

    double shooterPower = 0.4;

    // This declares the IMU needed to get the current direction the robot is facing
    IMU imu;

    @Override
    public void init() {
        frontLeftDrive = hardwareMap.get(DcMotor.class, "Front Left Drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "Front Right Drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "Back Left Drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "Back Right Drive");
        intakeMotor = hardwareMap.get(DcMotor.class, "Intake Motor");
        transferMotor = hardwareMap.get(DcMotor.class, "Transfer");
        shooterMotor = hardwareMap.get(DcMotor.class,"Shooter");

        flowerServo = hardwareMap.get(Servo.class, "flower"); // port 5 expansion hub


        // We set the left motors in reverse which is needed for drive trains where the left
        // motors are opposite to the right ones.
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);

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

    @Override
    public void loop() {

        DefaultControl();

        driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        telemetry.addData("Status", "Running");
        telemetry.addData("Shooter Power: ", shooterPower);
        telemetry.update();


    }

    // This routine drives the robot field relative
    private void driveFieldRelative(double forward, double right, double rotate) {
        // First, convert direction being asked to drive to polar coordinates
        double theta = Math.atan2(forward, right);
        double r = Math.hypot(right, forward);

        // Second, rotate angle by the angle the robot is pointing
        theta = AngleUnit.normalizeRadians(theta -
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        // Third, convert back to cartesian
        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);

        // Finally, call the drive method with robot relative forward and right amounts
        drive(newForward, newRight, rotate);

        if (gamepad1.a) {
            imu.resetYaw();
        }
    }

    public void drive(double forward, double right, double rotate) {
        // This calculates the power needed for *each wheel based on the amount of forward,
        // strafe right, and rotate
        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backRightPower = forward + right - rotate;
        double backLeftPower = forward - right + rotate;

        double maxPower = 1.0;
        double maxSpeed = 1.0;  // make this slower for outreaches

        if(gamepad1.right_trigger>0.8){
            maxSpeed=0.5;
        } else {
            maxSpeed=1.0;
        }

        // This is needed to make sure we don't pass > 1.0 to any wheel
        // It allows us to keep all of the motors in proportion to what they should
        // be and not get clipped
        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));

        // We multiply by maxSpeed so that it can be set lower for outreaches
        // When a young child is driving the robot, we may not want to allow full
        // speed.
        frontLeftDrive.setPower(maxSpeed * (frontLeftPower / maxPower));
        frontRightDrive.setPower(maxSpeed * (frontRightPower / maxPower));
        backLeftDrive.setPower(maxSpeed * (backLeftPower / maxPower));
        backRightDrive.setPower(maxSpeed * (backRightPower / maxPower));
    }

    public void DefaultControl(){
        //intake
        if (gamepad2.right_bumper) {
            intakeMotor.setPower(intakeSpeed);
        } else if (gamepad2.left_bumper) {
            intakeMotor.setPower(-intakeSpeed);
        } else {
            intakeMotor.setPower(0);
        }

        //transfer
        if(gamepad2.b){
            transferMotor.setPower(0.8);
        } else if(gamepad2.x){
            transferMotor.setPower(-0.8);
        } else{
            transferMotor.setPower(0);
        }


        if(gamepad2.right_trigger > 0.9){
            servoPos = 0.775;
        } else if (gamepad2.left_trigger > 0.9){
            servoPos = 0.19;
        }

        flowerServo.setPosition(servoPos);

        shooterControl();
    }

    public void shooterControl(){
        if(gamepad2.dpad_left){
            shooterPower = 0.4;
        } else if (gamepad2.dpad_right){
            shooterPower = 0.7;
        }

        if (gamepad2.dpadUpWasPressed()){
            shooterPower += 0.02;
        } else if (gamepad2.dpadDownWasPressed()){
            shooterPower -= 0.02;
        }

        shooterPower = Math.max(0, shooterPower);
        shooterPower = Math.min(1, shooterPower);

        shooterMotor.setPower(shooterPower);
    }

    public void testMotor(){
        if(gamepad1.dpad_up){
            frontLeftDrive.setPower(0.8);
        } else {
            frontLeftDrive.setPower(0);
        }

        if(gamepad1.dpad_left){
            frontRightDrive.setPower(0.8);
        } else {
            frontRightDrive.setPower(0);
        }

        if(gamepad1.dpad_down){
            backLeftDrive.setPower(0.8);
        } else {
            backLeftDrive.setPower(0);
        }

        if(gamepad1.dpad_right){
            backRightDrive.setPower(0.8);
        } else {
            backRightDrive.setPower(0);
        }
    }
}