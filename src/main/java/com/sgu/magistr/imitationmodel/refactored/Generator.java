package com.sgu.magistr.imitationmodel.refactored;

import java.util.*;

public class Generator {

    public final double CLASS_1_REQ_GEN_LAMBDA = 0.01; //                   интенсивность поступления требований 1 класса в МП
    public final double CLASS_2_REQ_GEN_LAMBDA = 15.0; //                   интенсивность поступления требований 2 класса в МП
    public final double MP_CLASS_1_REQ_PROC_LAMBDA = 200000; //             интенсивность обработки требований 1 класса в МП
    public final double MP_CLASS_2_REQ_PROC_LAMBDA = 400000; //             интенсивность обработки требований 2 класса в ПП
    public final double PP_REQ_PROC_LAMBDA = 25000;//                       интенсивность обработки требований ПП
    private final double TIME_OF_MODELING = 10000.0;

    List<Requirement> class1Queue = Collections.synchronizedList(new ArrayList<Requirement>());
    List<Requirement> class2Queue = Collections.synchronizedList(new ArrayList<Requirement>());
    private double currTime = 0;
    private Random random = new Random();

    private void emulate() {
        //генерация двух первых требований в МП(класс 1) и ПП(класс 2) в момент времени 0
        Requirement class1Req = new Requirement(currTime, 0, currTime + genExp(MP_CLASS_1_REQ_PROC_LAMBDA), 1);
        Requirement class2Req = new Requirement(currTime, 0, currTime + genExp(PP_REQ_PROC_LAMBDA), 2);
        Requirement curClass1Req;
        Requirement curClass2Req;

        class1Queue.add(class1Req);
        class2Queue.add(class2Req);

        System.out.println(class1Req + " queue count: " + class1Queue.size());
        System.out.println(class2Req + " queue count: " + class2Queue.size());

        double currTime = Math.min(class1Req.procStartTime, class2Req.procStartTime); // выбор текущего момента времени как минимум времени начала обслуживания одного из требований

        while (currTime < TIME_OF_MODELING) {
            double t1curr = 0.0; // время текущего события в МП очереди
            double t2curr = 0.0; // время текущего события в ПП очереди
            boolean processMP = false;
            boolean processPP = false;

            // выбор ближайшего события
            if (!class1Queue.isEmpty()) {
                if (class1Queue.size() > 1) { // если в очереди больше одного требования
                    if (class1Queue.get(0).releaseTime < class1Queue.get(class1Queue.size() - 1).procStartTime) { // если момент начала обслуживания последнего в очереди требования больше времени завершения обслуживания первого
                        t1curr = class1Queue.get(0).releaseTime; // фиксируем время завершения обслуживания первого поступившего в очередь МП
                        processMP = true;
                    } else {
                        t1curr = class1Queue.get(class1Queue.size() - 1).procStartTime; // иначе - время начала обработки последнего
                    }
                } else { // иначе - текущее время - время начала обслуживания текущего единственного в очереди требования
                    t1curr = class1Queue.get(0).procStartTime;
                }
            }
            // для ПП аналогично процедуре выбора текущего момента МП
            if (!class2Queue.isEmpty()) {
                if (class2Queue.size() > 1) {
                    if (class2Queue.get(0).releaseTime < class2Queue.get(class2Queue.size() - 1).procStartTime) {
                        t2curr = class2Queue.get(0).releaseTime;
                        processPP = true;
                    } else {
                        t2curr = class2Queue.get(class2Queue.size() - 1).procStartTime;
                    }
                } else {
                    t2curr = class2Queue.get(0).procStartTime;
                }
            }

            if (t1curr < t2curr) { // выбор минимального t текущего среди полученных из МП и ПП
                currTime = t1curr;

                if (processMP) { // если выставлен флаг начала обслуживания в МП
                    curClass1Req = class1Queue.get(1);
                    if (curClass1Req.procStartTime < currTime) { // если время начала обслуживания следующего требования меньше времени завершения обсл. текущего
                        curClass1Req.procStartTime = currTime; // Сдвигаем его на момент завершения певого, т.к. обсл. можно начинать как только обслужится предыдущее
                        if (curClass1Req.reqClass == 1) // если требование пришло извне
                            curClass1Req.releaseTime = currTime + genExp(MP_CLASS_1_REQ_PROC_LAMBDA); // задаем время завершения обслуживания согласно классу и типу устройства
                        else
                            curClass1Req.releaseTime = currTime + genExp(MP_CLASS_2_REQ_PROC_LAMBDA);
                    } else {
                        if (curClass1Req.reqClass == 1) // если требование пришло извне
                            curClass1Req.releaseTime = curClass1Req.procStartTime + genExp(MP_CLASS_1_REQ_PROC_LAMBDA); // задаем время завершения обслуживания согласно классу и типу устройства
                        else
                            curClass1Req.releaseTime = curClass1Req.procStartTime + genExp(MP_CLASS_2_REQ_PROC_LAMBDA);
                    }
                    class1Queue.get(0).procStartTime = currTime; // выставить время начала обслуживания при переходе в очередь ПП
                    class1Queue.get(0).reqClass = 3; // класс требования выставить в 3
                    class2Queue = changeQueue(class1Queue.get(0), class2Queue); // переход треб-я в ПП
                    if (class2Queue.indexOf(class1Queue.get(0)) == 0) {
                        class2Queue.get(0).releaseTime = currTime + genExp(PP_REQ_PROC_LAMBDA); // если при переходе в ПП требование оказалось первым в очереди, то задать время завершения обслуживания (понадобится для последующих витков цикла)
                    }
                    class1Queue.remove(class1Queue.get(0)); // требование выталкивается из МП очереди
                    System.out.println(curClass1Req + " queue 1 count: " + class1Queue.size());
                } else {
                    curClass1Req = class1Queue.get(class1Queue.size() - 1); // Если устройство не готово, генерируем новое требование относительно времени генерации последнего в очереди
                    if (curClass1Req.reqClass == 1) {
                        Requirement newClass1Req = new Requirement(currTime + genExp(CLASS_1_REQ_GEN_LAMBDA), 0, 0, 1);
                        class1Queue.add(newClass1Req);
                    }
                }
            } else {
                currTime = t2curr;

                if (processPP) { // Для ПП аналогично, только учитывается что время обработки одинаково для 2 и 3 класса и если коасс 3, то вытолкнуть из системы, если 2, то передать в очередь МП в позицию относительно времени перехода
                    curClass2Req = class2Queue.get(1);
                    if (curClass2Req.procStartTime < currTime) {
                        curClass2Req.procStartTime = currTime;
                        curClass2Req.releaseTime = currTime + genExp(PP_REQ_PROC_LAMBDA);
                    } else {
                        curClass2Req.releaseTime = curClass2Req.procStartTime + genExp(PP_REQ_PROC_LAMBDA);
                    }

                    if (class2Queue.get(0).reqClass == 2) {
                        class2Queue.get(0).procStartTime = currTime;
                        class1Queue = changeQueue(class2Queue.get(0), class1Queue);
                        if (class1Queue.indexOf(class2Queue.get(0)) == 0) {
                            class1Queue.get(0).releaseTime = currTime + genExp(MP_CLASS_2_REQ_PROC_LAMBDA);
                        }
                    }
                    class2Queue.remove(class2Queue.get(0));
                    System.out.println(curClass2Req + " queue 2 count: " + class2Queue.size());

                } else {
                    curClass2Req = class2Queue.get(class2Queue.size() - 1);
                    if (curClass2Req.reqClass == 2) {
                        Requirement newClass2Req = new Requirement(currTime + genExp(CLASS_2_REQ_GEN_LAMBDA), 0, 0, 2);
                        class2Queue.add(newClass2Req);
                    }
                }
            }

        }
    }

    // организация вставки требования в нужную позицию целевой очереди
    // позже перееделаю в LinkedList =)
    private List<Requirement> changeQueue(Requirement req, List<Requirement> queue) {
        List<Requirement> tmpQueue = new ArrayList<Requirement>();
        for (int i = 0; i < queue.size(); i++) {
            if (req.procStartTime < queue.get(i).generationTime) {
                tmpQueue.add(i,req);
                tmpQueue.add(queue.get(i));
            } else {
                tmpQueue.add(queue.get(i));
            }
        }
        return tmpQueue;
    }

    private double genExp(double lambda) {
        return -(Math.log(random.nextDouble()) / lambda);
    }

    public static void main(String[] args) {
        new Generator().emulate();
    }

    class Requirement {
        private double procStartTime;   //                фактическое время начала обслуживания
        private double releaseTime;     //                время завершения обслуживания
        private double procTime;        //                время обслуживания
        private double generationTime;  //                время поступления требования в систему

        private int reqClass;

        public Requirement(double procStartTime, double procTime, double releaseTime, int reqClass) {
            this.procStartTime = procStartTime;
            this.procTime = procTime;
            this.reqClass = reqClass;
            this.releaseTime = releaseTime;
            this.generationTime = procStartTime;
        }

        @Override
        public String toString() {
            return "Class: " + reqClass + " Generated at: " + String.format("%.10f", generationTime) + " Process started: " + String.format("%.10f", procStartTime) + " Processing time: " + String.format("%.10f", procTime) + " Release time: " + String.format("%.10f", releaseTime);
        }
    }
}