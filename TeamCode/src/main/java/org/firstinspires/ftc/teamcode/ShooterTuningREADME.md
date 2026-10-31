# Shooter PID Tuning

How to tune the shooter speed controller. Do the steps in order, because each one depends on the one before it.

The gains are at the top of `ShooterPID.java`:

```java
public static double kP = 0.0005;
public static double kI = 0.00025;
public static double kD = 0;
public static double kF = 1.0 / 6000;
```

The motor constants are near the top of `basicTeleOp.java`:

```java
static final double SHOOTER_MAX_RPM = 6000;
static final double SHOOTER_TICKS_PER_REV = 28;
```

## Using FTC Dashboard

FTC Dashboard lets you change the gains and graph the RPM while the robot runs.

1. Connect your laptop to the robot's Wi-Fi.
2. In a browser, open `http://192.168.43.1:8080/dash` for a Control Hub, or `http://192.168.49.1:8080/dash` for a phone Robot Controller.
3. Initialize and start `BIOBUZZ TELEOP`.
4. In the Configuration panel, open `ShooterPID`, edit a gain, and save it. It takes effect right away.
5. In the Graph panel, select `Shooter Target RPM:` and `Shooter RPM:`.

**Dashboard changes are lost when the robot restarts.** Copy your final gains into `ShooterPID.java` and redeploy.

Dashboard is for practice only. Disconnect your laptop from the robot's Wi-Fi before a match.

## What each gain does

| Gain | What it does | Too high |
|------|--------------|----------|
| `kF` | Supplies the base power for the target speed | Settles above the target |
| `kP` | Pushes harder the further the speed is from the target | RPM oscillates |
| `kI` | Slowly removes error that stays around | Overshoots and is slow to settle |
| `kD` | Resists fast changes in speed | Power jumps around from noisy readings |

## Before you start

- Charge the battery. Use a similarly charged battery for every test, because voltage changes the top speed.
- Keep hands clear. The shooter spins as soon as you press Start.
- Check that the encoder cable is plugged in. Without it, the shooter is driven toward full power.
- Have gamepad 2 ready and watch these telemetry lines: `Shooter Target RPM`, `Shooter RPM`, `Shooter Power`.

Controls on gamepad 2:

| Button | Action |
|--------|--------|
| D-pad left | Low preset (2400 RPM) |
| D-pad right | High preset (4200 RPM) |
| D-pad up / down | Target +100 / -100 RPM |

## Step 1: Check the motor constants

Find your motor's encoder resolution on its spec sheet and set `SHOOTER_TICKS_PER_REV`.

If the flywheel is geared to the motor and you want flywheel RPM, use the encoder ticks per motor revolution × the gear ratio (motor turns per flywheel turn).

Then run the shooter and check that `Shooter RPM` changes when the wheel spins. **If it stays at 0, stop right away** and fix the encoder connection.

## Step 2: Measure the max RPM

1. At the end of `shooterControl()`, temporarily change the last line to:
   ```java
   shooterMotor.setPower(1.0);
   ```
2. Deploy, press Start, and let the flywheel reach full speed.
3. Write down `Shooter RPM`.
4. Set `SHOOTER_MAX_RPM` to that number, and set `kF` in `ShooterPID.java` to `1.0 / ` that number.
5. Change the line back to `shooterMotor.setPower(shooterPower);` and redeploy.

## Step 3: Tune kF

In the dashboard, set `kP`, `kI`, and `kD` to 0.

1. Use the high preset and wait for the speed to settle.
2. Compare `Shooter RPM` to `Shooter Target RPM`.
3. If they differ by more than about 100 RPM, adjust `kF`:

   ```
   new kF = current kF × target RPM ÷ measured RPM
   ```

4. Check the low preset. It should also be close. Split the difference if one preset reads high and the other low.

The speed doesn't need to be exact here. `kP` and `kI` handle what's left.

## Step 4: Tune kP

Start at `kP = 0.0002`, with `kI` and `kD` still at 0.

1. Switch between the presets and watch how quickly `Shooter RPM` reaches the target.
2. Fire a shot and watch how far the RPM drops and how quickly it recovers.
3. Double `kP` and repeat until the RPM starts to oscillate around the target.
4. Set `kP` to half of the value that oscillated.

## Step 5: Tune kI (only if needed)

Skip this step if `Shooter RPM` settles within about 50 RPM of the target.

If it stays consistently off target:

1. Start at `kI = 0.0001`.
2. Raise it in small steps until the error goes away within a second or two.
3. If the speed overshoots after spin-up or drifts slowly up and down, lower it.

## Step 6: Leave kD at 0

Most flywheels don't need `kD`, and velocity readings are noisy enough that it can make the power jumpy. Only try a very small value if the RPM still overshoots after tuning `kP` and `kI`.

## Step 7: Set your shooting speeds

Take test shots from your real shooting positions and adjust the target with D-pad up and down. When you find speeds that work, update the presets in `shooterControl()`:

```java
if(gamepad2.dpad_left){
    shooterTargetRPM = 2400;   // low preset
} else if (gamepad2.dpad_right){
    shooterTargetRPM = 4200;   // high preset
}
```

Also update `double shooterTargetRPM = 2400;` near the top of the file, which is the speed the shooter uses when the OpMode starts.

Keep each preset below your measured max RPM, ideally by 10% or more. At the max, `Shooter Power` is already at 1.0, so there's no power left to recover after a shot.

## Troubleshooting

| What you see | Likely cause | Fix |
|--------------|--------------|-----|
| `Shooter RPM` stays at 0 and the wheel spins | Encoder cable is loose or in the wrong port | Stop and fix the wiring |
| Shooter goes to full power at any target | RPM reading is wrong | Check the encoder and `SHOOTER_TICKS_PER_REV` |
| RPM settles below the target | `kF` is too low, or the target is above what the motor can reach | Raise `kF`, or lower the target |
| RPM settles above the target | `kF` is too high | Lower `kF` |
| RPM swings above and below the target | `kP` or `kI` is too high | Lower `kP` first, then `kI` |
| Slow recovery after a shot | `kP` is too low | Raise `kP` |
| Big overshoot after spin-up | `kI` is too high | Lower `kI` |
| Shots get weaker through a match | Battery voltage is dropping and power is at 1.0 | Lower the preset or use a fresh battery |

## Final values

Fill this in once the shooter is tuned, and copy the gains into `ShooterPID.java`.

| Value | Result |
|-------|--------|
| Measured max RPM | |
| `kF` | |
| `kP` | |
| `kI` | |
| `kD` | |
| Low preset RPM | |
| High preset RPM | |
