package com.sgu.magistr.analiticsmodel;

import com.sgu.magistr.StatsGraphBuild;

import java.io.FileWriter;
import java.io.IOException;

public class AModel {
    private double L01 = 0.0; //интенсивность потока в систему S1 (микропроцессор)
    private double L02 = 0.0; //интенсивность потока в в систему S2 (приемо-передатчик)
    private double L0; // общая интенсивность поступления требований в узел
    private static final double MU11 = 200000.0;  //интенсивность обслуживания требований класса 1 в системе S1
    private static final double MU12 = 400000.0;//  интенсивность обслуживания требований класса 2 в системе S1
    private static final double MU2 = 25000.0;//  интенсивность обслуживания требований классов 1 и 2 в системе S2
    private static final double BETTA = 1000.0; //параметр экспон ф-ции распред-я длит восстановления узла

    private static FileWriter writer = null;
    private static final int Q_CNT = 20;//  интенсивность обслуживания требований классов 1 и 2 в системе S2

    private double p1[] = new double[Q_CNT];
    private double p2[] = new double[Q_CNT];
    private double p23[] = new double[Q_CNT];
    private double s01, s02 = 0.0;
    private double s, p = 0.0;
    private double q11, q12, q22, q23 = 0.0;
    private double u11, u12, u22, u23 = 0.0;

    private int cellNum;

    private void setL0(double l1){
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

        writer.write("L01: " + L01);
        writer.write("\nМО числа требований 1 класса в S1: " + q11);

        StatsGraphBuild.updateCell(1, cellNum, L01);
        StatsGraphBuild.updateCell(2, cellNum, q11);
        s += q11;
        for (int n = 0; n < Q_CNT; n++) {
            q12 += (n + 1) * p2[n];
        }
        writer.write("\nМО числа требований 2 класса в S1: " + q12);
        StatsGraphBuild.updateCell(3, cellNum, q12);
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

        writer.write(
                "\nМО числа требований 2 класса в S2: " + q22 +
                        "\nМО числа требований 3 класса в S2: " + q23);

        StatsGraphBuild.updateCell(4, cellNum, q22);
        StatsGraphBuild.updateCell(5, cellNum, q23);

        s = s + p;

        writer.write(
                "\nМО числа требований в сети: " + s +
                        "\nВремя реакции сети: " + s / L0 +
                        "\nВероятность, что узел пуст: " + s01 * s02 +
                        "\nВремя реакции сети: " + s / L0 +
                        "\nМО числа потерянных пакетов: " + L01 / BETTA

        );

        StatsGraphBuild.updateCell(6, cellNum, s);

        return s;
    }

    private void calcS2S2TimeCharacteristics() throws IOException {
        u11 = q11 / L01;
        u12 = q12 / L02;
        u22 = q22 / L0;
        u23 = q23 / L02;

        writer.write(
                "\nМО длительности пребывания требований 1 класса в S1: " + u11 +
                        "\nМО длительности пребывания требований 2 класса в S1: " + u12 +
                        "\nМО длительности пребывания требований 2 класса в S2: " + u22 +
                        "\nМО длительности пребывания требований 3 класса в S2: " + u23 + "\n\n"
        );

        StatsGraphBuild.updateCell(7, cellNum, u11);
        StatsGraphBuild.updateCell(8, cellNum, u12);
        StatsGraphBuild.updateCell(9, cellNum, u22);
        StatsGraphBuild.updateCell(10, cellNum, u23);
    }

/*    public static void main(String[] args) throws IOException {
        writer = new FileWriter("AM_RES.txt", false);
        AModel mm = new AModel();
        mm.calcS1Characteristics();
        mm.calcS2Characteristics();
        mm.calcS2S2TimeCharacteristics();
        writer.flush();
    }*/

    public AModel(double l1, FileWriter writer, int cellNum) throws IOException {
        this.writer = writer;
        this.cellNum = cellNum;
        setL0(l1);
        calcS1Characteristics();
        calcS2Characteristics();
        calcS2S2TimeCharacteristics();
        writer.flush();
    }

}
