package com.sgu.magistr.imitationmodel.refactored;

import java.util.Random;

public class Generator {

    public final double PACK_GEN_LAMBDA = 3;
    public final double PACK_PROC_LAMBDA = 4;
    private final int TIME_OF_MODELING = 100;

    private double currTime = 0;
    private Random random = new Random();
    private Packet queuedPacket;

    private void emulate(){
        while (currTime < TIME_OF_MODELING){
            currTime += genExp(PACK_GEN_LAMBDA);
            Packet curPacket = new Packet(currTime,genExp(PACK_PROC_LAMBDA));
            curPacket.setReleaseTime(currTime + curPacket.getProcTime());
            if (queuedPacket != null) {
                if (curPacket.getGenTime() < queuedPacket.getReleaseTime()) {
                    currTime = queuedPacket.getReleaseTime();
                    curPacket.setReleaseTime(curPacket.releaseTime + (currTime - curPacket.getGenTime()));
                }
            }
            queuedPacket = curPacket;
            System.out.println("Generation time: " + curPacket.genTime + " Release time: " + curPacket.releaseTime);
        }
    }

    private double genExp(double lambda){
        return -1*Math.log(random.nextDouble())/lambda;
    }

    public static void main(String[] args) {
        new Generator().emulate();
    }


    class Packet{
        private double genTime;
        private double releaseTime;
        private double procTime;

        public Packet(double genTime, double procTime){
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
            return releaseTime;
        }

        public void setReleaseTime(double releaseTime) {
            this.releaseTime = releaseTime;
        }
    }
}
