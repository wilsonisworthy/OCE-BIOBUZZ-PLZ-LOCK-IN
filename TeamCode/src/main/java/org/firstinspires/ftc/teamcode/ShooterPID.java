package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

// Velocity PID for the shooter. Velocities are in RPM, output is motor power.
public class ShooterPID {
    private final double kP;
    private final double kI;
    private final double kD;
    private final double kF;

    // Limits how much power the I term can add, so it doesn't wind up during spin-up
    private static final double MAX_INTEGRAL_POWER = 0.25;

    private final ElapsedTime timer = new ElapsedTime();
    private double integral = 0;
    private double lastVelocity = 0;
    private boolean firstRun = true;

    public ShooterPID(double kP, double kI, double kD, double kF) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
    }

    public double calculate(double targetVelocity, double currentVelocity) {
        double error = targetVelocity - currentVelocity;
        double derivative = 0;

        // The first call has no previous reading to measure time or change against
        if (!firstRun) {
            double dt = timer.seconds();

            if (dt > 0) {
                if (kI > 0) {
                    integral += error * dt;
                    double maxIntegral = MAX_INTEGRAL_POWER / kI;
                    integral = Math.max(-maxIntegral, Math.min(maxIntegral, integral));
                }

                // Uses the change in velocity, not error, so changing the target doesn't kick the motor
                derivative = -(currentVelocity - lastVelocity) / dt;
            }
        }

        timer.reset();
        lastVelocity = currentVelocity;
        firstRun = false;

        double power = kF * targetVelocity + kP * error + kI * integral + kD * derivative;

        // The flywheel only spins one way, so let it coast down instead of reversing
        return Math.max(0, Math.min(1, power));
    }

    public void reset() {
        integral = 0;
        lastVelocity = 0;
        firstRun = true;
    }
}
