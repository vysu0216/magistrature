package com.sgu.magistr.analiticsmodel;

public class ModelMain {

    private static final double BETTA = 1000.0; //параметр экспон ф-ции распред-я длит восстановления узла
    private static final double L01 = 0.05; //интенсивность потока в систему S1 (микропроцессор)
    private static final double L02 = 50.0; //интенсивность потока в в систему S2 (приемо-передатчик)
    private static final double L0 = L01 + L02; // общая интенсивность поступления требований в узел
    private static final double MU11 = 200000;  //интенсивность обслуживания требований класса 1 в системе S1
    private static final double MU12 = 400000;//  интенсивность обслуживания требований класса 2 в системе S1
    private static final double MU2 = 25000;//  интенсивность обслуживания требований классов 1 и 2 в системе S2

    private static final int Q_CNT = 20;//  интенсивность обслуживания требований классов 1 и 2 в системе S2

    private double p1[] = new double[Q_CNT];
    private double p2[] = new double[Q_CNT];
    private double p3[] = new double[Q_CNT];
    private double s01, s02 = 0.0;
    private double s, p = 0.0;

    //число сочетаний из n по m
    private double NS(int n, int m) {
        double a = 1.0;
        for (int i = 1; i <= n - m; i++) {
            a = a * (m + i) / i;
        }
        return a;
    }

    //Вычисление характеристики для для 1-й СМО
    private double calcS1Characteristics() {
        System.out.println(Q_CNT);
        for (int n = 0; n < Q_CNT; n++) {
            p1[n] = 0;
            p2[n] = 0;
        }
        for (int n = 1; n <= Q_CNT; n++) {
            for (int m = 0; m <= n; m++) {
                p = NS(n, m) * Math.pow(L01 / MU11, m) * Math.pow(L02 / MU12, n - m);
                if (m > 0)
                    p1[m-1] = p1[m-1] + p;
                if (n - m > 0)
                    p2[n - 1 - m] = p2[n - 1 - m] + p;
            }
            s += p;
        }
        s01 = 1 / (1 + s);
        for (int n = 0; n < Q_CNT; n++) {
            p1[n] = p1[n] * s01;
            p2[n] = p2[n] * s01;
        }
        s = 0;
        p = 0;
        for (int n = 0; n < Q_CNT; n++) {
            p = p + (n + 1) * p1[n];
        }
        System.out.println("1 Class in S1: " + p);
        s += p;
        p = 0;
        for (int n = 0; n < Q_CNT; n++) {
            p = p + (n + 1) * p2[n];
        }
        System.out.println("2 Class in S1 " + p);
        s += p;

        return s;
    }

    public static void main(String[] args) {
        new ModelMain().calcS1Characteristics();
    }

}
