package com.sgu.magistr.imitationmodel.refactored;

import java.util.*;

public class Generator {

    private Requirement newRequirement;
    public final double PACK_GEN_LAMBDA = 3;
    public final double PACK_PROC_LAMBDA = 5;
    private final int TIME_OF_MODELING = 100;

    List<Requirement> queue = Collections.synchronizedList(new ArrayList<Requirement>());
    private double currTime = 0;
    private Random random = new Random();

    private void emulate() {

        //Requirement initRequirement = new Requirement(1, 2);
        Requirement initRequirement = new Requirement(currTime + genExp(PACK_GEN_LAMBDA), genExp(PACK_PROC_LAMBDA));
        queue.add(initRequirement);
        iterateByQueue(queue);
    }

    private void iterateByQueue(List<Requirement> queue) {
        boolean t = false;
        for (int i = 0;;i++ ) {

            if (i == queue.size() - 1) {
                newRequirement = new Requirement(queue.get(i).genTime + genExp(PACK_GEN_LAMBDA), genExp(PACK_PROC_LAMBDA));
                System.out.println(queue.get(i) + " queue count: " + (i));
                t = true;
                queue.add(newRequirement);
            } else newRequirement = queue.get(i + 1);

            if (newRequirement.genTime > queue.get(0).releaseTime) {
                queue.remove(0);
                i--;
                iterateByQueue(queue);
            }

            if (newRequirement.releaseTime > TIME_OF_MODELING)
                break;
        }

    }

    private double genExp(double lambda) {
        return -1 * Math.log(random.nextDouble()) / lambda;
    }

    public static void main(String[] args) {
        new Generator().emulate();
    }


    class Requirement {
        private double genTime;
        private double releaseTime;
        private double procTime;

        public Requirement(double genTime, double procTime) {
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
