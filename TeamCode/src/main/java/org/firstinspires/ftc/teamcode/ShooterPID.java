package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;

// Velocity PID for the shooter. Velocities are in RPM, output is motor power.
@Config
public class ShooterPID {
    // Starting gains, these need to be tuned on the robot
    // FTC Dashboard can change these while the robot runs, but copy the final values here
    // because dashboard changes are lost when the robot restarts
    public static double kP = 0.0005;
    public static double kI = 0.00025;
    public static double kD = 0;
    public static double kF = 1.0 / 6000;

    // Limits how much power the I term can add, so it doesn't wind up during spin-up
    private static final double MAX_INTEGRAL_POWER = 0.25;

    private final ElapsedTime timer = new ElapsedTime();
    private double integralPower = 0;
    private double lastVelocity = 0;
    private boolean firstRun = true;

    public double calculate(double targetVelocity, double currentVelocity) {
        double error = targetVelocity - currentVelocity;
        double derivative = 0;

        // The first call has no previous reading to measure time or change against
        if (!firstRun) {
            double dt = timer.seconds();

            if (dt > 0) {
                // Stored as power so changing kI from the dashboard doesn't make the power jump
                if (kI > 0) {
                    integralPower += kI * error * dt;
                    integralPower = Math.max(-MAX_INTEGRAL_POWER, Math.min(MAX_INTEGRAL_POWER, integralPower));
                } else {
                    integralPower = 0;
                }

                // Uses the change in velocity, not error, so changing the target doesn't kick the motor
                derivative = -(currentVelocity - lastVelocity) / dt;
            }
        }

        timer.reset();
        lastVelocity = currentVelocity;
        firstRun = false;

        double power = kF * targetVelocity + kP * error + integralPower + kD * derivative;

        // The flywheel only spins one way, so let it coast down instead of reversing
        return Math.max(0, Math.min(1, power));
    }

    public void reset() {
        integralPower = 0;
        lastVelocity = 0;
        firstRun = true;
    }
}
