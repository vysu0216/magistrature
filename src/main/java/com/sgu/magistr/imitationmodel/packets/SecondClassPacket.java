package com.sgu.magistr.imitationmodel.packets;

public class SecondClassPacket extends Packet{

    private static final String NAME = "Second Class Requirement";

    public SecondClassPacket() {
        super(2, 1123);
    }

    @Override
    public String toString() {
        return NAME;
    }
}
