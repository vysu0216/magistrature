package com.sgu.magistr.imitationmodel;

import com.sgu.magistr.imitationmodel.packets.FirstClassPacket;
import com.sgu.magistr.imitationmodel.packets.Packet;
import com.sgu.magistr.imitationmodel.packets.SecondClassPacket;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.in;
import static java.lang.System.setOut;

public class Emulator {

    private static final Class PACKET_CLASS_TYPES[] = {FirstClassPacket.class, SecondClassPacket.class};
    private final int FULL_GENERATION_TIME = 30;    // Pakets generation time in seconds
    private final int GENERATION_TIME_DEGREE = 2;   // Degree of packets generation time in seconds

    private void emulate() {
        Generator<Packet> generator = new Generator();
        BlockingDeque<Packet> packets = generator.getPacketsQueue(PACKET_CLASS_TYPES, FULL_GENERATION_TIME, GENERATION_TIME_DEGREE); //start packets generating in queue
        while (!packets.isEmpty() || !generator.isProcessed()) {
            try {
                Packet packet = packets.takeFirst();
                processPacket(packet);
                System.out.println(packet.toString()
                        + ":\n Requirement generation time (millis): " + packet.getStartTime()
                        + "\n Requirement processed at (millis): " + packet.getEndTime()
                        + "\n Requirement serving time: " + packet.getLifeTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processPacket(Packet packet) throws InterruptedException {
        Thread.sleep(packet.PROCESSING_TIME);
        packet.setEndTime(currentTimeMillis());
    }

    public static void main(String[] args) {
        new Emulator().emulate();
    }

}
