package com.sgu.magistr.imitationmodel.refactored;

import java.util.*;

public class Generator {

    public final double CLASS_1_REQ_GEN_LAMBDA = 5; //                  лямбда для генерации требования 1 класса
    public final double CLASS_2_REQ_GEN_LAMBDA = 5; //                  лямбда для генерации требования 2 класса
    public final double MP_CLASS_1_REQ_PROC_LAMBDA = 200000; //         лямбда для генерации времени обработки требования 1 класса в МП
    public final double MP_CLASS_2_REQ_PROC_LAMBDA = 210000; //         лямбда для генерации времени обработки требования 2 класса в ПП
    public final double PP_REQ_PROC_LAMBDA = 400000;//                  лямбда для генерации времени обработки требований в ПП
    private final int TIME_OF_MODELING = 50;

    List<Requirement> class1Queue = Collections.synchronizedList(new ArrayList<Requirement>());
    List<Requirement> class2Queue = Collections.synchronizedList(new ArrayList<Requirement>());
    private double currTime = 0;
    private Random random = new Random();

    private void emulate() {
        Requirement class1Req = new Requirement(currTime, genExp(MP_CLASS_1_REQ_PROC_LAMBDA), currTime + genExp(MP_CLASS_1_REQ_PROC_LAMBDA), 1);
        Requirement class2Req = new Requirement(currTime, genExp(PP_REQ_PROC_LAMBDA), currTime + genExp(PP_REQ_PROC_LAMBDA), 2);

        double currTime = Math.min(class1Req.getGenTime(), class2Req.getGenTime());
        class1Queue.add(class1Req);
        class2Queue.add(class2Req);
        class1Queue.add(new Requirement(currTime + genExp(CLASS_1_REQ_GEN_LAMBDA), 0, 0, 1));
        class2Queue.add(new Requirement(currTime + genExp(CLASS_2_REQ_GEN_LAMBDA), 0, 0, 2));

        for(int i = 0; i < class1Queue.size(); i++){
            System.out.println(class1Queue.get(i) + " queue count: " + class1Queue.size());
        }
        for(int i = 0; i < class2Queue.size(); i++){
            System.out.println(class2Queue.get(i) + " queue count: " + class2Queue.size());
        }

        while (currTime < TIME_OF_MODELING) {

        }
    }

/*    @Deprecated
    private void iterateByQueue(List<Requirement> queue) {
        int i = 0;
        while (newRequirement.releaseTime < TIME_OF_MODELING) {


            if (i == queue.size() - 1) {
                newRequirement = new Requirement(queue.get(i).genTime + genExp(CLASS_1_REQ_GEN_LAMBDA), genExp(MP_CLASS_1_REQ_PROC_LAMBDA), 1000, 1);
                System.out.println(queue.get(i) + " queue count: " + (i));
                queue.add(newRequirement);
            } else newRequirement = queue.get(i + 1);

            if (newRequirement.genTime > queue.get(0).releaseTime) {
                queue.remove(0);
                i = 0;
            } else i++;
        }
    }*/

    private double genExp(double lambda) {
        return -(Math.log(random.nextDouble()) / lambda);
    }

    public static void main(String[] args) {
        new Generator().emulate();
    }

    class Requirement {
        private double genTime;
        private double releaseTime;
        private double procTime;

        private int reqClass;

        public Requirement(double genTime, double procTime, double releaseTime, int reqClass) {
            this.genTime = genTime;
            this.procTime = procTime;
            this.reqClass = reqClass;
            this.releaseTime = releaseTime;
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

        public int getReqClass() {
            return reqClass;
        }

        public void setReqClass(int reqClass) {
            this.reqClass = reqClass;
        }

        @Override
        public String toString() {
            return "Create time: " + String.format("%.10f", genTime) + " Processing time: " + String.format("%.10f", procTime) + " Release time: " + String.format("%.10f", releaseTime);
        }
    }
}