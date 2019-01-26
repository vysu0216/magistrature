package com.sgu.magistr.imitationmodel.refactored;

import java.util.*;

public class Generator {

    private static final double CLASS_1_REQ_GEN_LAMBDA = //1; //                               интенсивность поступления требований 1 класса в МП
    0.01;
    private static final double CLASS_2_REQ_GEN_LAMBDA = //1; //                               интенсивность поступления требований 2 класса в МП
    15.0;
    private static final double MP_CLASS_1_REQ_PROC_LAMBDA = //3; //                           интенсивность обработки требований 1 класса в МП
    200000;
    private static final double MP_CLASS_2_REQ_PROC_LAMBDA = //3; //                            интенсивность обработки требований 2 класса в ПП
    400000;
    private static final double PP_REQ_PROC_LAMBDA = //4;//                                     интенсивность обработки требований ПП
    25000;
    private static final double TIME_OF_MODELING = 100000.0;

    private List<Requirement> class1Queue = Collections.synchronizedList(new ArrayList<Requirement>()); //  очередь требований 1 класса
    private List<Requirement> class2Queue = Collections.synchronizedList(new ArrayList<Requirement>()); //  очередь требований 2 класса
    private Set<Event> EventsList = new HashSet<Event>();                                               //  список генерируемых событий

    private double currTime = 0;
    private Random random = new Random();

    private boolean isBusyMP = false;
    private boolean isBusyPP = false;

    private void emulate() {

        Requirement curReq = null;
        Requirement nextReq = null;
        EventsList.add(new Event(currTime, EventTypesEnum.SEND_TO_MP_CLASS_1));
        EventsList.add(new Event(currTime, EventTypesEnum.SEND_TO_PP_CLASS_2));

        double TMP = 0.0; //        Сумма времени пребывания всех требований в микропроцессоре
        double TPP = 0.0; //        Сумма времени пребывания всех требований в приемопередатчике
        int NMP = 1; //             Сумма требований поступающих в микропроцессор
        int NPP = 1; //             Сумма требований поступающих в приемопередатчик
        double MMP = 0.0; //        Оценка матожидания времени пребывания требований в микропроцессоре
        double MPP = 0.0; //        Оценка матожидания времени пребывания требований в приемопередатчике

        while (currTime < TIME_OF_MODELING) {
            Event currEvent = getMinTimeEvent(EventsList);
            currTime = currEvent.getTime();

            switch (currEvent.getEventType()) {
                case SEND_TO_MP_CLASS_1:
                    curReq = new Requirement(currTime, 1);
                    class1Queue.add(curReq);
                    EventsList.add(new Event(currTime + genExp(CLASS_1_REQ_GEN_LAMBDA), EventTypesEnum.SEND_TO_MP_CLASS_1));
                    if (!isBusyMP) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + genExp(MP_CLASS_1_REQ_PROC_LAMBDA), EventTypesEnum.FINISH_SERVE_IN_MP));
                        isBusyMP = true;
                    }
                    break;
                case SEND_TO_PP_CLASS_2:
                    curReq = new Requirement(currTime, 2);
                    class2Queue.add(curReq);
                    EventsList.add(new Event(currTime + genExp(CLASS_2_REQ_GEN_LAMBDA), EventTypesEnum.SEND_TO_PP_CLASS_2));
                    if (!isBusyPP) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + genExp(PP_REQ_PROC_LAMBDA), EventTypesEnum.FINISH_SERVE_IN_PP));
                        isBusyPP = true;
                    }
                    break;

                case SEND_TO_PP_CLASS_3:
                    //  curReq = new Requirement(currEvent.getGenTime(), 3);
                    curReq = new Requirement(currTime, 3);
                    class2Queue.add(curReq);
                    if (!isBusyPP) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + genExp(PP_REQ_PROC_LAMBDA), EventTypesEnum.FINISH_SERVE_CLASS_3));
                        isBusyPP = true;
                    }
                    break;

                case SEND_TO_MP_CLASS_2:
                    curReq = new Requirement(currTime, 2);
                    class1Queue.add(curReq);
                    if (!isBusyMP) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + genExp(MP_CLASS_2_REQ_PROC_LAMBDA), EventTypesEnum.FINISH_SERVE_IN_MP));
                        isBusyMP = true;
                    }
                    break;

                case FINISH_SERVE_IN_MP:
                    curReq = class1Queue.get(0);
                    TMP += currTime - curReq.getGenerationTime();
                    NMP++;
                    EventsList.add(new Event(currTime, curReq.getGenerationTime(), EventTypesEnum.SEND_TO_PP_CLASS_3));
                    class1Queue.remove(curReq);
                    if (!class1Queue.isEmpty()) {
                        nextReq = class1Queue.get(0);
                        nextReq.setProcStartTime(currTime);
                        if (nextReq.getReqClass() == 1)
                            EventsList.add(new Event(currTime + genExp(MP_CLASS_1_REQ_PROC_LAMBDA), EventTypesEnum.FINISH_SERVE_IN_MP));
                        else
                            EventsList.add(new Event(currTime + genExp(MP_CLASS_2_REQ_PROC_LAMBDA), EventTypesEnum.FINISH_SERVE_IN_MP));
                        isBusyMP = true;
                    } else
                        isBusyMP = false;
                    break;

                case FINISH_SERVE_IN_PP:
                    NPP++;
                    curReq = class2Queue.get(0);
                    TPP += currTime - curReq.getGenerationTime();
                    EventsList.add(new Event(currTime, curReq.getGenerationTime(), EventTypesEnum.SEND_TO_MP_CLASS_2));
                    class2Queue.remove(curReq);
                    if (!class2Queue.isEmpty()) {
                        nextReq = class2Queue.get(0);
                        nextReq.setProcStartTime(currTime);
                        if (nextReq.getReqClass() == 2)
                            EventsList.add(new Event(currTime + genExp(PP_REQ_PROC_LAMBDA), EventTypesEnum.FINISH_SERVE_IN_PP));
                        else
                            EventsList.add(new Event(currTime + genExp(PP_REQ_PROC_LAMBDA), EventTypesEnum.FINISH_SERVE_CLASS_3));
                        isBusyPP = true;
                    } else
                        isBusyPP = false;
                    break;

                case FINISH_SERVE_CLASS_3:
                    NPP++;
                    curReq = class2Queue.get(0);
                    TPP += currTime - curReq.getGenerationTime();
                    curReq.setReleaseTime(currTime);
                    class2Queue.remove(curReq);
                    if (!class2Queue.isEmpty()) {
                        nextReq = class2Queue.get(0);
                        nextReq.setProcStartTime(currTime);
                        if (nextReq.getReqClass() == 2)
                            EventsList.add(new Event(currTime + genExp(PP_REQ_PROC_LAMBDA), EventTypesEnum.FINISH_SERVE_IN_PP));
                        else
                            EventsList.add(new Event(currTime + genExp(PP_REQ_PROC_LAMBDA), EventTypesEnum.FINISH_SERVE_CLASS_3));
                        isBusyPP = true;
                    } else
                        isBusyPP = false;
                    break;
            }
            EventsList.remove(currEvent);
        }
        MMP = TMP / NMP;
        MPP = TPP / NPP;
        System.out.println("Оценка матожидания пребывания требований в приемопередатчике = " + MMP + "\n" +
                "Оценка матожидания пребывания требований в приемопередатчике = " + MPP);
    }

    private Event getMinTimeEvent(Set<Event> eventsList) {
        Event minTimeEvent = null;
        double mintime = TIME_OF_MODELING * 2;
        double eTime;
        for (Event event : eventsList) {
            if ((eTime = event.getTime()) < mintime) {
                minTimeEvent = event;
                mintime = eTime;
            }
        }
        return minTimeEvent;
    }

    private double genExp(double lambda) {
        return -(Math.log(random.nextDouble()) / lambda);
    }

    public static void main(String[] args) {
        new Generator().emulate();
    }

}