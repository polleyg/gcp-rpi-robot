package me.atomishere.rpibotclient;

/**
 * Created by archieoconnor on 16/12/17.
 */
public interface IRobotControls {
    /**
     * Stop the robot forward.
     */
    void stop();

    /**
     * Move the robot forward.
     */
    void forward();

    /**
     * Move the robot backwards.
     */
    void backwards();

    /**
     * Turn the robot right.
     */
    void right();

    /**
     * Turn the robot left.
     */
    void left();

    /**
     * Accelerate the robot.
     */
    void accelerate();

    /**
     * Decelerate the robot.
     */
    void decelerate();
}
