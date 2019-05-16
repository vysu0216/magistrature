package com.sgu.magistr.imitationmodel;

import com.sgu.magistr.StatsGraphBuild;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class IModel {

    private double L01 = 0.0;                                           //интенсивность поступления требований 1 класса в МП
    private double L02 = 0.0;                                           //интенсивность поступления требований 2 класса в МП
    private static final double MP_CLASS_1_REQ_PROC_MU = 1.0 / 200000;  //интенсивность обработки требований 1 класса в МП
    private static final double MP_CLASS_2_REQ_PROC_MU = 1.0 / 400000;  //интенсивность обработки требований 2 класса в ПП
    private static final double PP_REQ_PROC_MU = 1.0 / 25000;           //интенсивность обработки требований ПП
    private double tMod = 0.0;

    private List<Requirement> class1Queue = Collections.synchronizedList(new ArrayList<Requirement>()); // очередь требований 1 класса
    private List<Requirement> class2Queue = Collections.synchronizedList(new ArrayList<Requirement>()); // очередь требований 2 класса
    private Set<Event> EventsList = new HashSet<Event>(); // список генерируемых событий
    private Map<Integer, Double> mp1KMap = new HashMap<Integer, Double>(); // ассоц. массив для сумм k эл-тов в очереди МП
    private Map<Integer, Double> mp2KMap = new HashMap<Integer, Double>(); // ассоц. массив для сумм k эл-тов в очереди МП
    private Map<Integer, Double> pp2KMap = new HashMap<Integer, Double>(); // ассоц. массив для сумм k эл-тов в очереди МП
    private Map<Integer, Double> pp3KMap = new HashMap<Integer, Double>(); // ассоц. массив для сумм k эл-тов в очереди МП
    private Random random = new Random();

    private double currTime = 0; //     Значение текущего момента времени
    private double MPprevTime = 0.0; // Значение предыдущего момента времени для МП
    private double PPprevTime = 0.0; // Значение предыдущего момента времени для ПП
    private boolean isBusyMP = false; //Флаг занятости микропроцессора
    private boolean isBusyPP = false; //Флаг занятости приемопередатчика

    private static FileWriter writer = null;

    private int cellNum;

    private void setL0(double l1) {
        L01 = l1;
        L02 = L01 * 1000.0;
        double L0 = L01 + L02;
        tMod = 100000 / L0;
    }

    /**
     * Управляющая подпрограмма.
     */
    private void emulate() throws IOException {
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

        while (currTime < tMod) {
            Event currEvent = getMinTimeEvent(EventsList);
            currTime = currEvent.getTime();

            switch (currEvent.getEventType()) {
                case SEND_TO_MP_CLASS_1:
                    curReq = new Requirement(currTime, 1);
                    mTimeAccumulator("MP_1", getQueueCount(class1Queue, 1), currTime - MPprevTime);
                    MPprevTime = currTime;
                    class1Queue.add(curReq);
                    EventsList.add(new Event(currTime + genExp(L01), EventTypesEnum.SEND_TO_MP_CLASS_1));
                    if (!isBusyMP) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + MP_CLASS_1_REQ_PROC_MU/*genExp(MP_CLASS_1_REQ_PROC_MU)*/, EventTypesEnum.FINISH_SERVE_IN_MP));
                        isBusyMP = true;
                    }
                    break;
                case SEND_TO_PP_CLASS_2:
                    curReq = new Requirement(currTime, 2);
                    mTimeAccumulator("PP_2", getQueueCount(class2Queue, 2), currTime - PPprevTime);
                    PPprevTime = currTime;
                    class2Queue.add(curReq);
                    EventsList.add(new Event(currTime + genExp(L02), EventTypesEnum.SEND_TO_PP_CLASS_2));
                    if (!isBusyPP) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + PP_REQ_PROC_MU/*genExp(PP_REQ_PROC_MU)*/, EventTypesEnum.FINISH_SERVE_IN_PP));
                        isBusyPP = true;
                    }
                    break;

                case SEND_TO_PP_CLASS_3:
                    curReq = new Requirement(currTime, 3);
                    mTimeAccumulator("PP_3", getQueueCount(class2Queue, 3), currTime - PPprevTime);
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
                    mTimeAccumulator("MP_2", getQueueCount(class1Queue, 2), currTime - MPprevTime);
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
                        mTimeAccumulator("MP_1", getQueueCount(class1Queue, 1), currTime - MPprevTime);
                    } else if (curReq.getReqClass() == 2) {
                        TMP_2 += currTime - curReq.getGenerationTime();
                        NMP_2++;
                        mTimeAccumulator("MP_2", getQueueCount(class1Queue, 2), currTime - MPprevTime);
                    }
                    EventsList.add(new Event(currTime, curReq.getGenerationTime(), EventTypesEnum.SEND_TO_PP_CLASS_3));
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
                    mTimeAccumulator("PP_2", getQueueCount(class2Queue, 2), currTime - PPprevTime);
                    PPprevTime = currTime;
                    class2Queue.remove(curReq);
                    evalMath();
                    break;

                case FINISH_SERVE_CLASS_3:
                    NPP_3++;
                    curReq = class2Queue.get(0);
                    TPP_3 += currTime - curReq.getGenerationTime();
                    curReq.setReleaseTime(currTime);
                    mTimeAccumulator("PP_3", getQueueCount(class2Queue, 3), currTime - PPprevTime);
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
        double mp1Cnt = calculateMCount(mp1KMap);
        double mp2Cnt = calculateMCount(mp2KMap);
        double pp2Cnt = calculateMCount(pp2KMap);
        double pp3Cnt = calculateMCount(pp3KMap);

        writer.write("L01: " + L01 +
                "\nМО числа требований 1 класса в S1:  = " + mp1Cnt + "\n" +
                "МО числа требований 2 класса в S1: " + mp2Cnt + "\n" +
                "МО числа требований 2 класса в S2: " + pp2Cnt + "\n" +
                "МО числа требований 3 класса в S2: " + pp3Cnt + "\n" +
                "МО числа требований в сети: " + (mp1Cnt + pp2Cnt + mp2Cnt + pp3Cnt) + "\n" +
                "МО длительности пребывания требований 1 класса в S1: " + MMP_1 + "\n" +
                "МО длительности пребывания требований 2 класса в S1: " + MMP_2 + "\n" +
                "МО длительности пребывания требований 2 класса в S2: " + MPP_2 + "\n" +
                "МО длительности пребывания требований 3 класса в S2: " + MPP_3 + "\n\n");

        StatsGraphBuild.updateCell(13, cellNum, L01);
        StatsGraphBuild.updateCell(14, cellNum, mp1Cnt);
        StatsGraphBuild.updateCell(15, cellNum, mp2Cnt);
        StatsGraphBuild.updateCell(16, cellNum, pp2Cnt);
        StatsGraphBuild.updateCell(17, cellNum, pp3Cnt);
        StatsGraphBuild.updateCell(18, cellNum, (mp1Cnt + pp2Cnt + mp2Cnt + pp3Cnt));
        StatsGraphBuild.updateCell(19, cellNum, MMP_1);
        StatsGraphBuild.updateCell(20, cellNum, MMP_2);
        StatsGraphBuild.updateCell(21, cellNum, MPP_2);
        StatsGraphBuild.updateCell(22, cellNum, MPP_3);
    }

    private int getQueueCount(List<Requirement> queue, int classNum){
        int counter = 0;
        for (Requirement req : queue) {
            if(req.getReqClass() == classNum)
                counter++;
        }
        return counter;
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
        double mintime = tMod * 2;
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
        if (systemType.equals("MP_1")) {
            if (!mp1KMap.containsKey(queSize)) {
                mp1KMap.put(queSize, interval);
            } else {
                mp1KMap.put(queSize, mp1KMap.get(queSize) + interval);
            }
        } else if (systemType.equals("MP_2")) {
            if (!mp2KMap.containsKey(queSize)) {
                mp2KMap.put(queSize, interval);
            } else {
                mp2KMap.put(queSize, mp2KMap.get(queSize) + interval);
            }
        }else if (systemType.equals("PP_2")) {
            if (!pp2KMap.containsKey(queSize)) {
                pp2KMap.put(queSize, interval);
            } else {
                pp2KMap.put(queSize, pp2KMap.get(queSize) + interval);
            }
        } else {
            if (!pp3KMap.containsKey(queSize)) {
                pp3KMap.put(queSize, interval);
            } else {
                pp3KMap.put(queSize, pp3KMap.get(queSize) + interval);
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
            mCount += ((k * kMap.get(k)) / tMod);
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

    public IModel(double l1, FileWriter writer, int cellNum) throws IOException {
        this.writer = writer;
        this.cellNum = cellNum;
        setL0(l1);
        emulate();
    }

}