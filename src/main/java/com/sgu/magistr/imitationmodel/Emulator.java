package com.sgu.magistr.imitationmodel;

import com.sgu.magistr.imitationmodel.packets.FirstClassPacket;
import com.sgu.magistr.imitationmodel.packets.Packet;
import com.sgu.magistr.imitationmodel.packets.SecondClassPacket;

public class Emulator {

    private static final Class PACKET_CLASS_TYPES[] = {FirstClassPacket.class, SecondClassPacket.class};

    public static void main(String[] args) {
        Generator<Packet> generator = new Generator();
        Packet randomClaasPacket = generator.generateInstanceFromParent(PACKET_CLASS_TYPES);
        System.out.println(randomClaasPacket);
    }

}
