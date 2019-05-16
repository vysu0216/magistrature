package com.sgu.magistr.imitationmodel;

import java.util.*;

public class Generator {

    private static final double CLASS_1_REQ_GEN_LAMBDA = 10.0;//    интенсивность поступления требований 1 класса в МП
    private static final double CLASS_2_REQ_GEN_LAMBDA =
            CLASS_1_REQ_GEN_LAMBDA * 1000.0;
    //15.0;//    интенсивность поступления требований 2 класса в МП
    private static final double MP_CLASS_1_REQ_PROC_MU = 1.0 / 200000;//  интенсивность обработки требований 1 класса в МП
    private static final double MP_CLASS_2_REQ_PROC_MU = 1.0 / 400000;//  интенсивность обработки требований 2 класса в ПП
    private static final double PP_REQ_PROC_MU = 1.0 / 25000;//           интенсивность обработки требований ПП
    private static final double TIME_OF_MODELING = 100000.0;

    private List<Requirement> class1Queue = Collections.synchronizedList(new ArrayList<Requirement>()); // очередь требований 1 класса
    private List<Requirement> class2Queue = Collections.synchronizedList(new ArrayList<Requirement>()); // очередь требований 2 класса
    private Set<Event> EventsList = new HashSet<Event>(); // список генерируемых событий
    private Map<Integer, Double> mpKMap = new HashMap<Integer, Double>(); // ассоц. массив для сумм k эл-тов в очереди МП
    private Map<Integer, Double> ppKMap = new HashMap<Integer, Double>(); // ассоц. массив для сумм k эл-тов в очереди МП
    private Random random = new Random();

    private double currTime = 0; //     Значение текущего момента времени
    private double MPprevTime = 0.0; // Значение предыдущего момента времени для МП
    private double PPprevTime = 0.0; // Значение предыдущего момента времени для ПП
    private boolean isBusyMP = false; //Флаг занятости микропроцессора
    private boolean isBusyPP = false; //Флаг занятости приемопередатчика

    /**
     * Управляющая подпрограмма.
     */
    private void emulate() {
        Requirement curReq = null;
        Requirement nextReq = null;
        EventsList.add(new Event(currTime, EventTypesEnum.SEND_TO_MP_CLASS_1));
        EventsList.add(new Event(currTime, EventTypesEnum.SEND_TO_PP_CLASS_2));

        double TMP_1 = 0.0; //  Сумма длительностей пребывания требований 1 класса в МП
        double TMP_2 = 0.0; //  Сумма длительностей пребывания требований 2 класса в МП
        double TPP_2 = 0.0; //  Сумма длительностей пребывания всех требований в ПП
        double TPP_3 = 0.0; //  Сумма длительностей пребывания всех требований в ПП
        int NMP_1 = 1; //       Сумма требований 1 класса поступающих в микропроцессор
        int NMP_2 = 1; //       Сумма требований 2 класса поступающих в микропроцессор
        int NPP_2 = 1; //       Сумма требований 2 класса поступающих в приемопередатчик
        int NPP_3 = 1; //       Сумма требований 3 класса поступающих в приемопередатчик
        double MMP_1; //        Матожидание времени пребывания требований 1 класса в микропроцессоре
        double MMP_2; //        Матожидание времени пребывания требований 2 класса в микропроцессоре
        double MPP_2; //        Матожидание времени пребывания требований 2 класса в приемопередатчике
        double MPP_3; //        Матожидание времени пребывания требований 3 класса в приемопередатчике

        while (currTime < TIME_OF_MODELING) {
            Event currEvent = getMinTimeEvent(EventsList);
            currTime = currEvent.getTime();

            switch (currEvent.getEventType()) {
                case SEND_TO_MP_CLASS_1:
                    curReq = new Requirement(currTime, 1);
                    mTimeAccumulator("MP", class1Queue.size(), currTime - MPprevTime);
                    MPprevTime = currTime;
                    class1Queue.add(curReq);
                    EventsList.add(new Event(currTime + genExp(CLASS_1_REQ_GEN_LAMBDA), EventTypesEnum.SEND_TO_MP_CLASS_1));
                    if (!isBusyMP) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + MP_CLASS_1_REQ_PROC_MU/*genExp(MP_CLASS_1_REQ_PROC_MU)*/, EventTypesEnum.FINISH_SERVE_IN_MP));
                        isBusyMP = true;
                    }
                    break;
                case SEND_TO_PP_CLASS_2:
                    curReq = new Requirement(currTime, 2);
                    mTimeAccumulator("PP", class2Queue.size(), currTime - PPprevTime);
                    PPprevTime = currTime;
                    class2Queue.add(curReq);
                    EventsList.add(new Event(currTime + genExp(CLASS_2_REQ_GEN_LAMBDA), EventTypesEnum.SEND_TO_PP_CLASS_2));
                    if (!isBusyPP) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + PP_REQ_PROC_MU/*genExp(PP_REQ_PROC_MU)*/, EventTypesEnum.FINISH_SERVE_IN_PP));
                        isBusyPP = true;
                    }
                    break;

                case SEND_TO_PP_CLASS_3:
                    curReq = new Requirement(currTime, 3);
                    mTimeAccumulator("PP", class2Queue.size(), currTime - PPprevTime);
                    PPprevTime = currTime;
                    class2Queue.add(curReq);
                    if (!isBusyPP) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + PP_REQ_PROC_MU/* genExp(PP_REQ_PROC_MU)*/, EventTypesEnum.FINISH_SERVE_CLASS_3));
                        isBusyPP = true;
                    }
                    break;

                case SEND_TO_MP_CLASS_2:
                    curReq = new Requirement(currTime, 2);
                    mTimeAccumulator("MP", class1Queue.size(), currTime - MPprevTime);
                    class1Queue.add(curReq);
                    MPprevTime = currTime;
                    if (!isBusyMP) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + MP_CLASS_2_REQ_PROC_MU/*genExp(MP_CLASS_2_REQ_PROC_MU)*/, EventTypesEnum.FINISH_SERVE_IN_MP));
                        isBusyMP = true;
                    }
                    break;

                case FINISH_SERVE_IN_MP:
                    curReq = class1Queue.get(0);
                    if (curReq.getReqClass() == 1) {
                        TMP_1 += currTime - curReq.getGenerationTime();
                        NMP_1++;
                    } else if (curReq.getReqClass() == 2) {
                        TMP_2 += currTime - curReq.getGenerationTime();
                        NMP_2++;
                    }
                    EventsList.add(new Event(currTime, curReq.getGenerationTime(), EventTypesEnum.SEND_TO_PP_CLASS_3));
                    mTimeAccumulator("MP", class1Queue.size(), currTime - MPprevTime);
                    MPprevTime = currTime;
                    class1Queue.remove(curReq);
                    if (!class1Queue.isEmpty()) {
                        nextReq = class1Queue.get(0);
                        nextReq.setProcStartTime(currTime);
                        if (nextReq.getReqClass() == 1)
                            EventsList.add(new Event(currTime + MP_CLASS_1_REQ_PROC_MU/*genExp(MP_CLASS_1_REQ_PROC_MU)*/, EventTypesEnum.FINISH_SERVE_IN_MP));
                        else
                            EventsList.add(new Event(currTime + MP_CLASS_2_REQ_PROC_MU/*genExp(MP_CLASS_2_REQ_PROC_MU)*/, EventTypesEnum.FINISH_SERVE_IN_MP));
                        isBusyMP = true;
                    } else
                        isBusyMP = false;
                    break;

                case FINISH_SERVE_IN_PP:
                    curReq = class2Queue.get(0);
                    NPP_2++;
                    TPP_2 += currTime - curReq.getGenerationTime();
                    EventsList.add(new Event(currTime, curReq.getGenerationTime(), EventTypesEnum.SEND_TO_MP_CLASS_2));
                    mTimeAccumulator("PP", class2Queue.size(), currTime - PPprevTime);
                    PPprevTime = currTime;
                    class2Queue.remove(curReq);
                    evalMath();
                    break;

                case FINISH_SERVE_CLASS_3:
                    NPP_3++;
                    curReq = class2Queue.get(0);
                    TPP_3 += currTime - curReq.getGenerationTime();
                    curReq.setReleaseTime(currTime);
                    mTimeAccumulator("PP", class2Queue.size(), currTime - PPprevTime);
                    PPprevTime = currTime;
                    class2Queue.remove(curReq);
                    evalMath();
                    break;
            }
            EventsList.remove(currEvent);
        }
        MMP_1 = TMP_1 / NMP_1;
        MMP_2 = TMP_2 / NMP_2;
        MPP_2 = TPP_2 / NPP_2;
        MPP_3 = TPP_3 / NPP_3;
        double mpCnt = calculateMCount(mpKMap);
        double ppCnt = calculateMCount(ppKMap);
        System.out.println("Интенсивность поступления требований " + CLASS_1_REQ_GEN_LAMBDA +
                "\nМатематическое ожидание длительности пребывания требований 1 класса в микропроцессоре = " + MMP_1 + "\n" +
                "Математическое ожидание длительности пребывания требований 2 класса в микропроцессоре = " + MMP_2 + "\n" +
                "Математическое ожидание длительности пребывания требований 2 класса в приемопередатчике = " + MPP_2 + "\n" +
                "Математическое ожидание длительности пребывания требований 3 класса в приемопередатчике = " + MPP_3 + "\n" +
                "Математическое ожидание числа требований в сети = " + (mpCnt + ppCnt) + "\n" +
                "Математическое ожидание числа требований в микропроцессоре = " + mpCnt + "\n" +
                "Математическое ожидание числа требований в приемопередатчике = " + ppCnt);
    }

    private void evalMath() {
        Requirement nextReq;
        if (!class2Queue.isEmpty()) {
            nextReq = class2Queue.get(0);
            nextReq.setProcStartTime(currTime);
            if (nextReq.getReqClass() == 2)
                EventsList.add(new Event(currTime + PP_REQ_PROC_MU/*genExp(PP_REQ_PROC_MU)*/, EventTypesEnum.FINISH_SERVE_IN_PP));
            else
                EventsList.add(new Event(currTime + PP_REQ_PROC_MU/*genExp(PP_REQ_PROC_MU)*/, EventTypesEnum.FINISH_SERVE_CLASS_3));
            isBusyPP = true;
        } else
            isBusyPP = false;
    }

    /**
     * Получение ближайшего события.
     *
     * @param eventsList - текущий набор событий для выбора
     *                   события с минимальным моментом наступления
     */
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

    /**
     * Заполняет ассоциативный массив для каждого из k требований в очереди.
     *
     * @param systemType - значения типа очереди
     *                   для аккумулирования суммы длительностей пребывания
     * @param queSize    - текущий размер очереди
     * @param interval   - интервал для суммирования с предыдущими
     */
    private void mTimeAccumulator(String systemType, int queSize, double interval) {
        double tmpInterval = 0;
        if (systemType.equals("MP")) {
            if (!mpKMap.containsKey(queSize)) {
                mpKMap.put(queSize, interval);
            } else {
                mpKMap.put(queSize, mpKMap.get(queSize) + interval);
            }
        } else {
            if (!ppKMap.containsKey(queSize)) {
                ppKMap.put(queSize, interval);
            } else {
                ppKMap.put(queSize, ppKMap.get(queSize) + interval);
            }
        }
    }

    /**
     * Вычисление мат ожидания числа требований.
     *
     * @param kMap - ассоциативный массив сумм промежутков времени для k
     */
    private double calculateMCount(Map<Integer, Double> kMap) {
        double mCount = 0;

        for (Integer k : kMap.keySet()) {
            mCount += ((k * kMap.get(k)) / TIME_OF_MODELING);
        }
        return mCount;
    }

    /**
     * Генерация промежутков времени между типами событий.
     *
     * @param lambda параметр показательного распределения
     */
    private double genExp(double lambda) {
        return -(Math.log(random.nextDouble()) / lambda);
    }

    public static void main(String[] args) {
        new Generator().emulate();
    }

}