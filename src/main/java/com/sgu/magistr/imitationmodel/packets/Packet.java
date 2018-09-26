package com.sgu.magistr.imitationmodel.packets;

import static java.lang.System.currentTimeMillis;

public class Packet {
    final private long startTime = currentTimeMillis();

    private long endTime;
    private final int PACKET_CLASS;
    private static String NAME = "Base Packet";

    public Packet(int packetClass) {
        this.PACKET_CLASS = packetClass;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getPACKET_CLASS() {
        return PACKET_CLASS;
    }

    public void setEndTime() {
        endTime = System.currentTimeMillis() - startTime;
    }

    @Override
    public String toString() {
        return NAME;
    }
}
