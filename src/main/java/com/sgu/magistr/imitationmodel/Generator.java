package com.sgu.magistr.imitationmodel;

import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.setOut;

public class Generator<T> {

    private Random rand = new Random();
    private BlockingDeque<T> packetsQueue = new LinkedBlockingDeque<T>();

    private boolean isProcessed = false;

    private void generatePackets(Class[] packetClassTypes, int genTime, int denDegree) {
        long startTime = currentTimeMillis();
        try {
            while ((currentTimeMillis() - startTime) / 1000 < genTime) {
                int selector = rand.nextInt(packetClassTypes.length);
                Thread.sleep((long) (1000 * (rand.nextDouble() * denDegree)));
                packetsQueue.add((T) packetClassTypes[selector].newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isProcessed = true;
        }

    }

    public BlockingDeque<T> getPacketsQueue(final Class[] packetClassTypes, final int genTime, final int denDegree) {
        new Thread(new Runnable() {
            public void run() {
                generatePackets(packetClassTypes, genTime,  denDegree);
            }
        }).start();
        return packetsQueue;
    }

    public boolean isProcessed() {
        return isProcessed;
    }
}
