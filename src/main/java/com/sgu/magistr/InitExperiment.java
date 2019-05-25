package com.sgu.magistr;

import com.sgu.magistr.analiticsmodel.AModel;
import com.sgu.magistr.imitationmodel.IModel;

import java.io.FileWriter;
import java.io.IOException;

public class InitExperiment {

    private static final double[] l1Array = {0.01, 0.05, 0.1, 0.5, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};

    public static void main(String[] args) throws IOException {
        FileWriter writer = new FileWriter("RES.txt", false);
        writer.write("Данные аналитической модели: \n");
        for (int i = 0; i < l1Array.length; i++) {
            new AModel(l1Array[i], writer, i + 1);
        }
        /*writer.write("Данные имитационной модели: \n");
        for (int i = 0; i < l1Array.length; i++) {
            new IModel(l1Array[i], writer, i + 1);
        }*/
        writer.close();
    }
}
