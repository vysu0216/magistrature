package com.sgu.magistr.imitationmodel.refactored;

import java.util.*;

public class Generator {

    private static final double CLASS_1_REQ_GEN_LAMBDA = 1;//0.01; //                                           интенсивность поступления требований 1 класса в МП
    private static final double CLASS_2_REQ_GEN_LAMBDA = 1;//15.0; //                                           интенсивность поступления требований 2 класса в МП
    private static final double MP_CLASS_1_REQ_PROC_LAMBDA = 3;//200000; //                                     интенсивность обработки требований 1 класса в МП
    private static final double MP_CLASS_2_REQ_PROC_LAMBDA = 3;//400000; //                                     интенсивность обработки требований 2 класса в ПП
    private static final double PP_REQ_PROC_LAMBDA = 4;//25000;//                                               интенсивность обработки требований ПП
    private static final double TIME_OF_MODELING = 1000000;//10000.0;

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

        double timeAcc = 0.0;
        int N = 1;
        double mwAnalyze = 0.0;

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
                    /*timeAcc += currTime - curReq.getGenerationTime();
                    N++;*/
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
                    N++;
                    curReq = class2Queue.get(0);
                    timeAcc += currTime - curReq.getGenerationTime();
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
                    N++;
                    curReq = class2Queue.get(0);
                    timeAcc += currTime - curReq.getGenerationTime();
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
                    //System.out.println("Time accumulator = " + timeAcc + " N = " + N + " " + curReq);
                    break;
            }
            EventsList.remove(currEvent);
        }
        mwAnalyze = timeAcc / N;
        System.out.println(mwAnalyze);
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