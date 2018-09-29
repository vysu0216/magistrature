package com.sgu.magistr.imitationmodel.packets;

import static java.lang.System.currentTimeMillis;

public class Packet {
    final private long startTime = currentTimeMillis();

    private long endTime;
    private final int PACKET_CLASS;
    public final long PROCESSING_TIME;
    private static String NAME = "Base Requirement";

    public Packet(int packetClass, long processing_time) {
        this.PACKET_CLASS = packetClass;
        PROCESSING_TIME = processing_time;
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

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public float getLifeTime() {
        return (float)(endTime - startTime)/1000;
    }

    @Override
    public String toString() {
        return NAME;
    }
}
