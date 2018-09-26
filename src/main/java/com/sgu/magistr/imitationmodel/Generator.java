package com.sgu.magistr.imitationmodel;

import java.util.Random;

public class Generator<T> {

    private Random rand = new Random();

    public T generateInstanceFromParent(Class[] packetClassTypes){
        int selector = rand.nextInt(packetClassTypes.length);
        try {
            return (T) packetClassTypes[selector].newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
