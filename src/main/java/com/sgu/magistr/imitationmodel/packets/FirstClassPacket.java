package com.sgu.magistr.imitationmodel.packets;

public class FirstClassPacket extends Packet {

    private static final String NAME = "First Class Requirement";

    public FirstClassPacket() {
        super(1, 806);
    }

    @Override
    public String toString() {
        return NAME;
    }

}
