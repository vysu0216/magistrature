package com.sgu.magistr.analiticsmodel;

import com.sgu.magistr.StatsGraphBuild;

import java.io.FileWriter;
import java.io.IOException;

public class AModel {
    private double L01 = 0.0;                       //интенсивность потока в систему S1 (микропроцессор)
    private double L02 = 0.0;                       //интенсивность потока в в систему S2 (приемо-передатчик)
    private double L0;                              //общая интенсивность поступления требований в узел
    private static final double MU11 = 200000.0;    //интенсивность обслуживания требований класса 1 в системе S1
    private static final double MU12 = 400000.0;    //интенсивность обслуживания требований класса 2 в системе S1
    private static final double MU2 = 25000.0;      //интенсивность обслуживания требований классов 1 и 2 в системе S2
    private static final double ALPHA = 0.5;        //параметр экспон ф-ции распред-я длит восстановления узла
    private static final double BETTA = 1000.0;     //параметр экспон ф-ции распред-я длит восстановления узла
    private static final int Q_CNT = 20;            //интенсивность обслуживания требований классов 1 и 2 в системе S2

    private static FileWriter writer = null;

    private double p1[] = new double[Q_CNT];
    private double p2[] = new double[Q_CNT];
    private double p23[] = new double[Q_CNT];
    private double s01, s02 = 0.0;
    private double s, p = 0.0;
    private double q11, q12, q22, q23 = 0.0;
    private double u11, u12, u22, u23 = 0.0;

    private int cellNum;

    private void setL0(double l1) {
        L01 = l1;
        L02 = L01 * 1000.0;
        L0 = L01 + L02;
    }

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
        for (int n = 1; n <= Q_CNT; n++) {
            q11 = q11 + n * p1[n - 1];
        }
        s += q11;
        for (int n = 0; n < Q_CNT; n++) {
            q12 += (n + 1) * p2[n];
        }
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
        q22 = p * L02 / (L0 + L02);
        q23 = p * L0 / (L0 + L02);

        s = s + p;
        return s;
    }

    private void calcS2S2TimeCharacteristics() throws IOException {
        u11 = q11 / L01;
        u12 = q12 / L02;
        u22 = q22 / L0;
        u23 = q23 / L02;
    }


    private void writeOutput(int writeFlag) throws IOException {
        writer.write(
                "L01: " + L01 +
                        "\nМО числа требований 1 класса в S1: " + q11 +
                        "\nМО числа требований 2 класса в S1: " + q12 +
                        "\nМО числа требований 2 класса в S2: " + q22 +
                        "\nМО числа требований 3 класса в S2: " + q23 +
                        "\nМО числа требований в сети: " + s +
                        "\nВремя реакции сети: " + s / L0 +
                        "\nВероятность, что узел пуст: " + s01 * s02 +
                        "\nМО числа потерянных пакетов: " + L01 / BETTA +
                        "\nМО длительности пребывания требований 1 класса в S1: " + u11 +
                        "\nМО длительности пребывания требований 2 класса в S1: " + u12 +
                        "\nМО длительности пребывания требований 2 класса в S2: " + u22 +
                        "\nМО длительности пребывания требований 3 класса в S2: " + u23 + "\n\n"
        );

        StatsGraphBuild.updateCell(1 + writeFlag, cellNum, L01);
        StatsGraphBuild.updateCell(2 + writeFlag, cellNum, q11);
        StatsGraphBuild.updateCell(3 + writeFlag, cellNum, q12);
        StatsGraphBuild.updateCell(4 + writeFlag, cellNum, q22);
        StatsGraphBuild.updateCell(5 + writeFlag, cellNum, q23);
        StatsGraphBuild.updateCell(6 + writeFlag, cellNum, s);
        StatsGraphBuild.updateCell(7 + writeFlag, cellNum, u11);
        StatsGraphBuild.updateCell(8 + writeFlag, cellNum, u12);
        StatsGraphBuild.updateCell(9 + writeFlag, cellNum, u22);
        StatsGraphBuild.updateCell(10 + writeFlag, cellNum, u23);

        writer.flush();
    }

    public AModel(double l1, FileWriter writer, int cellNum) throws IOException {
        this.writer = writer;
        this.cellNum = cellNum;
        setL0(l1);
        calcS1Characteristics();
        calcS2Characteristics();
        calcS2S2TimeCharacteristics();
        writeOutput(0);
        binomReliability();
        writeOutput(12);
        writer.flush();
    }

    private double binomReliability() {
        double p = BETTA / (ALPHA + BETTA);
        double p1;
        double qq11 = 0.0;
        double qq12 = 0.0;
        double qq22 = 0.0;
        double qq23 = 0.0;
        double uu11 = 0.0;
        double uu12 = 0.0;
        double uu22 = 0.0;
        double uu23 = 0.0;
        double ss = 0.0;


        for (int i = 0; i < 1000; i++) {
            p1 = calculateReliability(1000, i, p);
            qq11 += q11 * p1;
            qq12 += q12 * p1;
            qq22 += q22 * p1;
            qq23 += q23 * p1;
            uu11 += u11 * p1;
            uu12 += u12 * p1;
            uu22 += u22 * p1;
            uu23 += u23 * p1;
            uu23 += u23 * p1;
            ss += s * p1;
        }

        q11 = qq11;
        q12 = qq12;
        q22 = qq22;
        q23 = qq23;
        u11 = uu11;
        u12 = uu12;
        u22 = uu22;
        u23 = uu23;
        s = ss;
        return 0;
    }

    private double calculateReliability(int n, int k, double p) {

        return NS(n, k) * Math.pow(p, k) * Math.pow(1.0 - p, n - k);
    }


}
