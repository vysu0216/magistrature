package com.sgu.magistr.imitationmodel;

import com.sgu.magistr.StatsGraphBuild;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class IModel {

    private final int eRowNum;
    private double L01 = 0.0;                           //интенсивность поступления требований 1 класса в S1
    private double L02 = 0.0;                           //интенсивность поступления требований 2 класса в S2
    private static final double MU11 = 1.0 / 200000;    //интенсивность обработки требований 1 класса в S1
    private static final double MU12 = 1.0 / 400000;    //интенсивность обработки требований 2 класса в S1
    private static final double MU23 = 1.0 / 25000;     //интенсивность обработки требований S2
    private double tMod = 0.0;                          //модельное время

    // очередь требований в систему S1:
    private List<Requirement> S1Queue = Collections.synchronizedList(new ArrayList<Requirement>());
    // очередь требований в систему S2:
    private List<Requirement> S2Queue = Collections.synchronizedList(new ArrayList<Requirement>());
    private Set<Event> EventsList = new HashSet<Event>();                  //список генерируемых событий
    private Map<Integer, Double> S11KMap = new HashMap<Integer, Double>(); //ассоц. массив для сумм k эл-тов 1 класса в очереди S1
    private Map<Integer, Double> S12KMap = new HashMap<Integer, Double>(); //ассоц. массив для сумм k эл-тов 2 класса в очереди S1
    private Map<Integer, Double> S22KMap = new HashMap<Integer, Double>(); //ассоц. массив для сумм k эл-тов 2 класса в очереди S2
    private Map<Integer, Double> S23KMap = new HashMap<Integer, Double>(); //ассоц. массив для сумм k эл-тов 3 класса в очереди S2
    private Random random = new Random();

    private double currTime = 0;        //Значение текущего момента времени
    private double S1prevTime = 0.0;    //Значение предыдущего момента времени для S1
    private double S2prevTime = 0.0;    //Значение предыдущего момента времени для S2
    private boolean isBusyS1 = false;   //Флаг занятости микропроцессора
    private boolean isBusyS2 = false;   //Флаг занятости приемопередатчика

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

        double TS11 = 0.0;      //Сумма длительностей пребывания требований 1 класса в S1
        double TS12 = 0.0;      //Сумма длительностей пребывания требований 2 класса в S1
        double TS22 = 0.0;      //Сумма длительностей пребывания всех требований в S2
        double TS23 = 0.0;      //Сумма длительностей пребывания всех требований в S2
        int NS11 = 1;           //Сумма требований 1 класса поступающих в S1
        int NS12 = 1;           //Сумма требований 2 класса поступающих в S1
        int NS22 = 1;           //Сумма требований 2 класса поступающих в S2
        int NS23 = 1;           //Сумма требований 3 класса поступающих в S2
        double MS11;            //МО времени пребывания требований 1 класса в S1
        double MS12;            //МО времени пребывания требований 2 класса в S1
        double MS22;            //МО времени пребывания требований 2 класса в S2
        double MS23;            //МО времени пребывания требований 3 класса в S2

        while (currTime < tMod) {
            Event currEvent = getMinTimeEvent(EventsList);
            currTime = currEvent.getTime();

            switch (currEvent.getEventType()) {
                case SEND_TO_MP_CLASS_1:
                    curReq = new Requirement(currTime, 1);
                    mTimeAccumulator("MP_1", getQueueCount(S1Queue, 1), currTime - S1prevTime);
                    S1prevTime = currTime;
                    S1Queue.add(curReq);
                    EventsList.add(new Event(currTime + genExp(L01), EventTypesEnum.SEND_TO_MP_CLASS_1));
                    if (!isBusyS1) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + MU11, EventTypesEnum.FINISH_SERVE_IN_MP));
                        isBusyS1 = true;
                    }
                    break;
                case SEND_TO_PP_CLASS_2:
                    curReq = new Requirement(currTime, 2);
                    mTimeAccumulator("PP_2", getQueueCount(S2Queue, 2), currTime - S2prevTime);
                    S2prevTime = currTime;
                    S2Queue.add(curReq);
                    EventsList.add(new Event(currTime + genExp(L02), EventTypesEnum.SEND_TO_PP_CLASS_2));
                    if (!isBusyS2) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + MU23, EventTypesEnum.FINISH_SERVE_IN_PP));
                        isBusyS2 = true;
                    }
                    break;

                case SEND_TO_PP_CLASS_3:
                    curReq = new Requirement(currTime, 3);
                    mTimeAccumulator("PP_3", getQueueCount(S2Queue, 3), currTime - S2prevTime);
                    S2prevTime = currTime;
                    S2Queue.add(curReq);
                    if (!isBusyS2) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + MU23, EventTypesEnum.FINISH_SERVE_CLASS_3));
                        isBusyS2 = true;
                    }
                    break;

                case SEND_TO_MP_CLASS_2:
                    curReq = new Requirement(currTime, 2);
                    mTimeAccumulator("MP_2", getQueueCount(S1Queue, 2), currTime - S1prevTime);
                    S1Queue.add(curReq);
                    S1prevTime = currTime;
                    if (!isBusyS1) {
                        curReq.setProcStartTime(currTime);
                        EventsList.add(new Event(currTime + MU12, EventTypesEnum.FINISH_SERVE_IN_MP));
                        isBusyS1 = true;
                    }
                    break;

                case FINISH_SERVE_IN_MP:
                    curReq = S1Queue.get(0);
                    if (curReq.getReqClass() == 1) {
                        TS11 += currTime - curReq.getGenerationTime();
                        NS11++;
                        mTimeAccumulator("MP_1", getQueueCount(S1Queue, 1), currTime - S1prevTime);
                    } else if (curReq.getReqClass() == 2) {
                        TS12 += currTime - curReq.getGenerationTime();
                        NS12++;
                        mTimeAccumulator("MP_2", getQueueCount(S1Queue, 2), currTime - S1prevTime);
                    }
                    EventsList.add(new Event(currTime, curReq.getGenerationTime(), EventTypesEnum.SEND_TO_PP_CLASS_3));
                    S1prevTime = currTime;
                    S1Queue.remove(curReq);
                    if (!S1Queue.isEmpty()) {
                        nextReq = S1Queue.get(0);
                        nextReq.setProcStartTime(currTime);
                        if (nextReq.getReqClass() == 1)
                            EventsList.add(new Event(currTime + MU11, EventTypesEnum.FINISH_SERVE_IN_MP));
                        else
                            EventsList.add(new Event(currTime + MU12, EventTypesEnum.FINISH_SERVE_IN_MP));
                        isBusyS1 = true;
                    } else
                        isBusyS1 = false;
                    break;

                case FINISH_SERVE_IN_PP:
                    curReq = S2Queue.get(0);
                    NS22++;
                    TS22 += currTime - curReq.getGenerationTime();
                    EventsList.add(new Event(currTime, curReq.getGenerationTime(), EventTypesEnum.SEND_TO_MP_CLASS_2));
                    mTimeAccumulator("PP_2", getQueueCount(S2Queue, 2), currTime - S2prevTime);
                    S2prevTime = currTime;
                    S2Queue.remove(curReq);
                    evalMath();
                    break;

                case FINISH_SERVE_CLASS_3:
                    NS23++;
                    curReq = S2Queue.get(0);
                    TS23 += currTime - curReq.getGenerationTime();
                    curReq.setReleaseTime(currTime);
                    mTimeAccumulator("PP_3", getQueueCount(S2Queue, 3), currTime - S2prevTime);
                    S2prevTime = currTime;
                    S2Queue.remove(curReq);
                    evalMath();
                    break;
            }
            EventsList.remove(currEvent);
        }
        MS11 = TS11 / NS11;
        MS12 = TS12 / NS12;
        MS22 = TS22 / NS22;
        MS23 = TS23 / NS23;
        double S11Cnt = calculateMCount(S11KMap);
        double S12Cnt = calculateMCount(S12KMap);
        double S22Cnt = calculateMCount(S22KMap);
        double S23Cnt = calculateMCount(S23KMap);

        writer.write("L01: " + L01 +
                "\nМО числа требований 1 класса в S1:  = " + S11Cnt + "\n" +
                "МО числа требований 2 класса в S1: " + S12Cnt + "\n" +
                "МО числа требований 2 класса в S2: " + S22Cnt + "\n" +
                "МО числа требований 3 класса в S2: " + S23Cnt + "\n" +
                "МО числа требований в сети: " + (S11Cnt + S22Cnt + S12Cnt + S23Cnt) + "\n" +
                "МО длительности пребывания требований 1 класса в S1: " + MS11 + "\n" +
                "МО длительности пребывания требований 2 класса в S1: " + MS12 + "\n" +
                "МО длительности пребывания требований 2 класса в S2: " + MS22 + "\n" +
                "МО длительности пребывания требований 3 класса в S2: " + MS23 + "\n\n");

        StatsGraphBuild.updateCell(13, cellNum, L01);
        StatsGraphBuild.updateCell(eRowNum + 14, cellNum, S11Cnt);
        StatsGraphBuild.updateCell(25, cellNum, S12Cnt);
        StatsGraphBuild.updateCell(26, cellNum, S22Cnt);
        StatsGraphBuild.updateCell(27, cellNum, S23Cnt);
        StatsGraphBuild.updateCell(28, cellNum, (S11Cnt + S22Cnt + S12Cnt + S23Cnt));
        StatsGraphBuild.updateCell(eRowNum + 29, cellNum, MS11);
        StatsGraphBuild.updateCell(40, cellNum, MS12);
        StatsGraphBuild.updateCell(41, cellNum, MS22);
        StatsGraphBuild.updateCell(42, cellNum, MS23);
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
        if (!S2Queue.isEmpty()) {
            nextReq = S2Queue.get(0);
            nextReq.setProcStartTime(currTime);
            if (nextReq.getReqClass() == 2)
                EventsList.add(new Event(currTime + MU23, EventTypesEnum.FINISH_SERVE_IN_PP));
            else
                EventsList.add(new Event(currTime + MU23, EventTypesEnum.FINISH_SERVE_CLASS_3));
            isBusyS2 = true;
        } else
            isBusyS2 = false;
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
            if (!S11KMap.containsKey(queSize)) {
                S11KMap.put(queSize, interval);
            } else {
                S11KMap.put(queSize, S11KMap.get(queSize) + interval);
            }
        } else if (systemType.equals("MP_2")) {
            if (!S12KMap.containsKey(queSize)) {
                S12KMap.put(queSize, interval);
            } else {
                S12KMap.put(queSize, S12KMap.get(queSize) + interval);
            }
        }else if (systemType.equals("PP_2")) {
            if (!S22KMap.containsKey(queSize)) {
                S22KMap.put(queSize, interval);
            } else {
                S22KMap.put(queSize, S22KMap.get(queSize) + interval);
            }
        } else {
            if (!S23KMap.containsKey(queSize)) {
                S23KMap.put(queSize, interval);
            } else {
                S23KMap.put(queSize, S23KMap.get(queSize) + interval);
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

    public IModel(double l1, FileWriter writer, int cellNum, int eRowNum) throws IOException {
        this.writer = writer;
        this.cellNum = cellNum;
        this.eRowNum = eRowNum;
        setL0(l1);
        emulate();
    }

}