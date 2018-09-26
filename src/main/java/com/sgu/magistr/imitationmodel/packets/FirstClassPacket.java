package com.sgu.magistr.imitationmodel.packets;

public class FirstClassPacket extends Packet {

    private static String NAME = "First Class Packet";

    public FirstClassPacket() {
        super(1);
    }

    @Override
    public String toString() {
        return NAME;
    }

}
