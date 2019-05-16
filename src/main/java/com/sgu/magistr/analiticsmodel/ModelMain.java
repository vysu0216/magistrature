package com.sgu.magistr.analiticsmodel;

import java.io.FileWriter;
import java.io.IOException;

public class ModelMain {

    private static final double BETTA = 1000.0; //параметр экспон ф-ции распред-я длит восстановления узла
    private static final double L01 = 1.0; //интенсивность потока в систему S1 (микропроцессор)
    private static final double L02 = 1000.0; //интенсивность потока в в систему S2 (приемо-передатчик)
    private static final double L0 = L01 + L02; // общая интенсивность поступления требований в узел
    private static final double MU11 = 200000.0;  //интенсивность обслуживания требований класса 1 в системе S1
    private static final double MU12 = 400000.0;//  интенсивность обслуживания требований класса 2 в системе S1
    private static final double MU2 = 25000.0;//  интенсивность обслуживания требований классов 1 и 2 в системе S2

    private static FileWriter writer = null;
    private static final int Q_CNT = 40;//  интенсивность обслуживания требований классов 1 и 2 в системе S2

    private double p1[] = new double[Q_CNT];
    private double p2[] = new double[Q_CNT];
    private double p23[] = new double[Q_CNT];
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
    private double calcS1Characteristics() throws IOException {
        System.out.println(Q_CNT);
        for (int n = 0; n < Q_CNT; n++) {
            p1[n] = 0;
            p2[n] = 0;
        }
        for (int n = 1; n <= Q_CNT; n++) {
            for (int m = 0; m <= n; m++) {
                p = NS(n, m) * Math.pow(L01 / MU11, m) * Math.pow(L02 / MU12, n - m);
                if (m > 0)
                    p1[m - 1] = p1[m - 1] + p;
                if (n - m > 0)
                    p2[n - 1 - m] = p2[n - 1 - m] + p;
                s += p;
            }
        }
        s01 = 1 / (1 + s);
        for (int n = 0; n < Q_CNT; n++) {
            p1[n] = p1[n] * s01;
            p2[n] = p2[n] * s01;
        }
        s = 0.0;
        p = 0.0;
        for (int n = 1; n <= Q_CNT; n++) {
            p = p + n * p1[n - 1];
        }
        System.out.println("МО числа требований 1 класса в S1: " + p);
        writer.write("МО числа требований 1 класса в S1: " + p);
        s += p;
        p = 0;
        for (int n = 0; n < Q_CNT; n++) {
            p = p + (n + 1) * p2[n];
        }
        System.out.println("МО числа требований 2 класса в S1: " + p);
        writer.write("\nМО числа требований 2 класса в S1: " + p);
        s += p;
        return s;
    }

    private double calcS2Characteristics() throws IOException {
        s02 = 1 - (L0 + L02) / MU2;

        for (int n = 1; n <= Q_CNT; n++) {
            p23[n - 1] = s02 * Math.pow((L0 + L02) / MU2, n);
        }

        p = 0.0;

        for (int n = 1; n <= Q_CNT; n++) {
            p = p + n * p23[n - 1];
        }

        System.out.println("МО числа требований 2 класса в S2: " + p * L02 / (L0 + L02));
        System.out.println("МО числа требований 3 класса в S2: " + p * L0 / (L0 + L02));

        writer.write("\nМО числа требований 2 класса в S2: " + p * L02 / (L0 + L02) +
                "\nМО числа требований 3 класса в S2: " + (p * L0 / (L0 + L02)));
        s = s + p;
        System.out.println("МО числа требований в сети: " + s);
        System.out.println("Время реакции сети: " + s / L0);
        System.out.println("Вероятность, того что узел пуст: " + s01 * s02);
        System.out.println("МО числа потерянных пакетов: " + L01 / BETTA);

        writer.write("\nМО числа требований в сети: " + s +
                "\nВремя реакции сети: " + s / L0 +
                "\nВероятность, что узел пуст: " + s01 * s02 +
                "\nВремя реакции сети: " + s / L0 +
                "\nМО числа потерянных пакетов: " + L01 / BETTA

        );

        return s;
    }

    public static void main(String[] args) throws IOException {

        writer = new FileWriter("RES.txt", false);
        ModelMain mm = new ModelMain();
        mm.calcS1Characteristics();
        mm.calcS2Characteristics();
        writer.flush();

    }

}
