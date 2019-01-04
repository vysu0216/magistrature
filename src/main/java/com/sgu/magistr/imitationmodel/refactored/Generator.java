package com.sgu.magistr.imitationmodel.refactored;

import java.util.*;

public class Generator {

    public final double CLASS_1_REQ_GEN_LAMBDA = 3; //                  лямбда для генерации требования 1 класса
    public final double CLASS_2_REQ_GEN_LAMBDA = 2; //                  лямбда для генерации требования 2 класса
    public final double MP_CLASS_1_REQ_PROC_LAMBDA = 4; //         лямбда для генерации времени обработки требования 1 класса в МП
    public final double MP_CLASS_2_REQ_PROC_LAMBDA = 5; //         лямбда для генерации времени обработки требования 2 класса в ПП
    public final double PP_REQ_PROC_LAMBDA = 6;//                  лямбда для генерации времени обработки требований в ПП
    private final double TIME_OF_MODELING = 10.0;

    List<Requirement> class1Queue = Collections.synchronizedList(new ArrayList<Requirement>());
    List<Requirement> class2Queue = Collections.synchronizedList(new ArrayList<Requirement>());
    private double currTime = 0;
    private Random random = new Random();

    private void emulate() {
        Requirement class1Req = new Requirement(currTime, 0, currTime + genExp(MP_CLASS_1_REQ_PROC_LAMBDA), 1);
        Requirement class2Req = new Requirement(currTime, 0, currTime + genExp(PP_REQ_PROC_LAMBDA), 2);

        class1Queue.add(class1Req);
        class2Queue.add(class2Req);

        System.out.println(class1Req + " queue count: " + class1Queue.size());
        System.out.println(class2Req + " queue count: " + class2Queue.size());

        double currTime = Math.min(class1Req.genTime, class2Req.genTime);

        while (currTime < TIME_OF_MODELING) {
            double t1curr = 0.0;
            double t2curr = 0.0;
            boolean processMP = false;
            boolean processPP = false;

            if (!class1Queue.isEmpty()) {
                if (class1Queue.size() > 1) {
                    if (class1Queue.get(0).releaseTime < class1Queue.get(class1Queue.size() - 1).genTime) {
                        t1curr = class1Queue.get(0).releaseTime;
                        processMP = true;
                    } else {
                        t1curr = class1Queue.get(class1Queue.size() - 1).genTime;
                    }
                } else {
                    t1curr = class1Queue.get(0).genTime;
                }
            }
            if (!class2Queue.isEmpty()) {
                if (class2Queue.size() > 1) {
                    if (class2Queue.get(0).releaseTime < class2Queue.get(class2Queue.size() - 1).genTime) {
                        t2curr = class2Queue.get(0).releaseTime;
                        processPP = true;
                    } else {
                        t2curr = class2Queue.get(class2Queue.size() - 1).genTime;
                    }
                } else {
                    t2curr = class2Queue.get(0).genTime;
                }
            }

            if (t1curr < t2curr) {
                currTime = t1curr;
                if (processMP) {
                    if (class1Queue.get(1).genTime < currTime) {
                        class1Queue.get(1).genTime = currTime;
                        class1Queue.get(1).releaseTime = currTime + genExp(MP_CLASS_1_REQ_PROC_LAMBDA);
                    } else
                        class1Queue.get(1).releaseTime = class1Queue.get(1).genTime + genExp(MP_CLASS_1_REQ_PROC_LAMBDA);
                    System.out.println(class1Queue.get(1) + " queue count: " + class1Queue.size());
                    class1Queue.remove(class1Queue.get(0));
                } else {
                    Requirement newClass1Req = new Requirement(currTime + genExp(CLASS_1_REQ_GEN_LAMBDA), 0, 0, 1);
                    class1Queue.add(newClass1Req);
                }
            } else {
                currTime = t2curr;
                if (processPP) {
                    if (class2Queue.get(1).genTime < currTime) {
                        class2Queue.get(1).genTime = currTime;
                        class2Queue.get(1).releaseTime = currTime + genExp(PP_REQ_PROC_LAMBDA);
                    } else
                        class2Queue.get(1).releaseTime = class2Queue.get(1).genTime + genExp(PP_REQ_PROC_LAMBDA);
                    System.out.println(class2Queue.get(1) + " queue count: " + class2Queue.size());
                    class2Queue.remove(class2Queue.get(0));
                } else {
                    Requirement newClass2Req = new Requirement(currTime + genExp(CLASS_2_REQ_GEN_LAMBDA), 0, 0, 2);
                    class2Queue.add(newClass2Req);
                }
            }

        }
    }

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

        @Override
        public String toString() {
            return "Class: " + reqClass + " Create time: " + String.format("%.10f", genTime) + " Processing time: " + String.format("%.10f", procTime) + " Release time: " + String.format("%.10f", releaseTime);
        }
    }
}