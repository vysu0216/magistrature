package com.sgu.magistr.imitationmodel.packets;

public class SecondClassPacket extends Packet{

    private static String NAME = "Second Class Packet";

    public SecondClassPacket() {
        super(2);
    }

    @Override
    public String toString() {
        return NAME;
    }
}
