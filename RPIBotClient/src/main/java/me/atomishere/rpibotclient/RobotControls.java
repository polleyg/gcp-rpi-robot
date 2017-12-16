package me.atomishere.rpibotclient;

/**
 * Created by archieoconnor on 16/12/17.
 */
class RobotControls implements IRobotControls {

    public void stop() {
        RpiBotClient.setData(0x00);
    }

    public void forward() {
        RpiBotClient.setData(0x01);
    }

    public void backwards() {
        RpiBotClient.setData(0x02);
    }

    public void right() {
        RpiBotClient.setData(0x03);
    }

    public void left() {
        RpiBotClient.setData(0x04);
    }

    public void accelerate() {
        RpiBotClient.setData(0x05);
    }

    public void decelerate() {
        RpiBotClient.setData(0x06);
    }
}
