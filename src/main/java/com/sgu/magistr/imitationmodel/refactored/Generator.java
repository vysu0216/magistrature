package com.sgu.magistr.imitationmodel.refactored;

import java.util.LinkedList;
import java.util.Random;

public class Generator {

    public final double PACK_GEN_LAMBDA = 3;
    public final double PACK_PROC_LAMBDA = 5;
    private final int TIME_OF_MODELING = 100;

    LinkedList<Packet> queue = new LinkedList();
    private double currTime = 0;
    private Random random = new Random();

    private void emulate() {
        currTime += genExp(PACK_GEN_LAMBDA);
        queue.addLast(new Packet(currTime, genExp(PACK_PROC_LAMBDA)));
        currTime += genExp(PACK_GEN_LAMBDA);
        queue.addLast(new Packet(currTime, genExp(PACK_PROC_LAMBDA)));

        iterateByQueue(queue);

        System.out.println(queue.getFirst() + "\n" + queue.getLast());

/*        while (currTime < TIME_OF_MODELING) {

        }*/
    }


    private void iterateByQueue(LinkedList<Packet> queue) {
        for (Packet packet : queue) {
            if (packet.genTime < queue.getFirst().getReleaseTime())
                if (packet == queue.getLast()) {
                    queue.addLast(new Packet(packet.genTime + genExp(PACK_GEN_LAMBDA), genExp(PACK_PROC_LAMBDA)));
                }
            else {
                    queue.removeFirst();
                    iterateByQueue(queue);
                }
        }
    }

    private double genExp(double lambda) {
        return -1 * Math.log(random.nextDouble()) / lambda;
    }

    public static void main(String[] args) {
        new Generator().emulate();
    }


    class Packet {
        private double genTime;
        private double releaseTime;
        private double procTime;

        public Packet(double genTime, double procTime) {
            this.genTime = genTime;
            this.procTime = procTime;
        }

        public double getProcTime() {
            return procTime;
        }

        public void setProcTime(double procTime) {
            this.procTime = procTime;
        }

        public double getGenTime() {
            return genTime;
        }

        public void setGenTime(double genTime) {
            this.genTime = genTime;
        }

        public double getReleaseTime() {
            return releaseTime = genTime + procTime;
        }

        public void setReleaseTime(double releaseTime) {
            this.releaseTime = releaseTime;
        }

        @Override
        public String toString() {
            return "Create time: " + genTime + " Processing time: " + procTime + " Release time: " + getReleaseTime();
        }
    }
}
