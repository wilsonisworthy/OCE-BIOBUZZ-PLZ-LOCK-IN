//
//package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//
//@Autonomous(name = "Basic Auto", group = "Robot")
////@Disabled
//public class basicAuto extends LinearOpMode {
//
//    DcMotor frontLeftDrive;
//    DcMotor frontRightDrive;
//    DcMotor backLeftDrive;
//    DcMotor backRightDrive;
//
//    @Override
//    public void runOpMode() {
//
//        frontLeftDrive = hardwareMap.get(
//                DcMotor.class, "Front Left Drive");
//        frontRightDrive = hardwareMap.get(
//                DcMotor.class, "Front Right Drive");
//        backLeftDrive = hardwareMap.get(
//                DcMotor.class, "Back Left Drive");
//        backRightDrive = hardwareMap.get(
//                DcMotor.class, "Back Right Drive");
//
//        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
//        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
//        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
//        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
//
//        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        telemetry.addData("Status: ", "Ready");
//        telemetry.update();
//
//        waitForStart();
//
//        if (opModeIsActive()) {
//
//            // Drive forward at 50% power
//            frontLeftDrive.setPower(0.5);
//            frontRightDrive.setPower(0.5);
//            backLeftDrive.setPower(0.5);
//            backRightDrive.setPower(0.5);
//
//            // Keep moving forward for 2 seconds
//            sleep(1000);
//
//            // Stop all four wheels
//            stopDrive();
//        }
//
//        stopDrive();
//    }
//
//    private void stopDrive() {
//        frontLeftDrive.setPower(0);
//        frontRightDrive.setPower(0);
//        backLeftDrive.setPower(0);
//        backRightDrive.setPower(0);
//    }
//}






package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Basic Auto", group = "Robot")
public class basicAuto extends LinearOpMode {

    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;

    DcMotor transferMotor;
    DcMotorEx shooterMotor;
    DcMotorEx intakeMotor;

    static final double SHOOTER_RPM = 2700;
    static final double TICKS_PER_REV = 28;

    static final double SPINUP_TOLERANCE = 150;
    static final double SHOT_RPM_DROP = 300;

    @Override
    public void runOpMode() {

        frontLeftDrive = hardwareMap.get(
                DcMotor.class, "Front Left Drive");
        frontRightDrive = hardwareMap.get(
                DcMotor.class, "Front Right Drive");
        backLeftDrive = hardwareMap.get(
                DcMotor.class, "Back Left Drive");
        backRightDrive = hardwareMap.get(
                DcMotor.class, "Back Right Drive");

        transferMotor = hardwareMap.get(
                DcMotor.class, "Transfer");
        shooterMotor = hardwareMap.get(
                DcMotorEx.class, "Shooter");
        intakeMotor = hardwareMap.get(
                DcMotorEx.class, "Intake Motor");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);

        transferMotor.setDirection(DcMotor.Direction.REVERSE);

        frontLeftDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);

        shooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterMotor.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.FLOAT);

        telemetry.addData("Status", "Ready");
        telemetry.update();

        waitForStart();

        if (!opModeIsActive()) return;

        ElapsedTime timer = new ElapsedTime();

        // STEP 1: START SHOOTER AND INTAKE
        shooterMotor.setVelocity(
                SHOOTER_RPM * TICKS_PER_REV / 60.0);

        intakeMotor.setPower(1);

        // STEP 2: WAIT FOR SHOOTER TO REACH SPEED
        boolean shooterReady = false;

        timer.reset();

        while (opModeIsActive() && timer.seconds() < 5) {

            // Keep intake running
            intakeMotor.setPower(1);

            double rpm = getShooterRPM();

            telemetry.addData("Status", "Spinning up");
            telemetry.addData("Target RPM", SHOOTER_RPM);
            telemetry.addData("Current RPM", rpm);
            telemetry.addData("Intake", "RUNNING");
            telemetry.update();

            if (rpm >= SHOOTER_RPM - SPINUP_TOLERANCE) {
                shooterReady = true;
                break;
            }

            idle();
        }

        // STEP 3: FEED AND SHOOT 3 BALLS
        boolean allBallsShot = shooterReady;

        if (shooterReady && opModeIsActive()) {

            transferMotor.setPower(1);

            for (int ball = 1; ball <= 3; ball++) {

                boolean shotDetected = false;

                // Wait for RPM drop caused by a ball
                timer.reset();

                while (opModeIsActive() && timer.seconds() < 4) {

                    // KEEP INTAKE RUNNING
                    intakeMotor.setPower(1);

                    double rpm = getShooterRPM();

                    telemetry.addData("Status", "Feeding balls");
                    telemetry.addData("Ball", ball);
                    telemetry.addData("Shooter RPM", rpm);
                    telemetry.addData("Transfer", "RUNNING");
                    telemetry.addData("Intake", "RUNNING");
                    telemetry.update();

                    if (rpm <= SHOOTER_RPM - SHOT_RPM_DROP) {
                        shotDetected = true;
                        break;
                    }

                    idle();
                }

                if (!shotDetected) {
                    allBallsShot = false;
                    break;
                }

                // Wait for shooter to recover before next shot
                timer.reset();

                boolean recovered = false;

                while (opModeIsActive() && timer.seconds() < 3) {

                    // KEEP INTAKE RUNNING DURING RECOVERY
                    intakeMotor.setPower(1);

                    double rpm = getShooterRPM();

                    telemetry.addData("Status", "Shooter recovering");
                    telemetry.addData("Ball detected", ball);
                    telemetry.addData("Shooter RPM", rpm);
                    telemetry.addData("Intake", "RUNNING");
                    telemetry.update();

                    if (rpm >= SHOOTER_RPM - SPINUP_TOLERANCE) {
                        recovered = true;
                        break;
                    }

                    idle();
                }

                if (!recovered) {
                    allBallsShot = false;
                    break;
                }

                telemetry.addData("Balls Shot", ball);
                telemetry.update();
            }
        }

        // STEP 4: STOP INTAKE AND TRANSFER AFTER SHOOTING
        transferMotor.setPower(0);
        intakeMotor.setPower(0);

        // STEP 5: DRIVE FORWARD ONLY AFTER 3 DETECTED SHOTS
        if (opModeIsActive() && allBallsShot) {

            telemetry.addData("Status", "3 balls shot - Driving");
            telemetry.update();

            frontLeftDrive.setPower(0.5);
            frontRightDrive.setPower(0.5);
            backLeftDrive.setPower(0.5);
            backRightDrive.setPower(0.5);

            sleep(1000);
        }

        // STEP 6: STOP EVERYTHING
        stopDrive();
        transferMotor.setPower(0);
        shooterMotor.setPower(0);
        intakeMotor.setPower(0);

        telemetry.addData("Status",
                allBallsShot ? "Finished" : "Shooting incomplete");
        telemetry.update();
    }

    private double getShooterRPM() {
        return Math.abs(shooterMotor.getVelocity())
                * 60.0 / TICKS_PER_REV;
    }

    private void stopDrive() {
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backLeftDrive.setPower(0);
        backRightDrive.setPower(0);
    }
}
