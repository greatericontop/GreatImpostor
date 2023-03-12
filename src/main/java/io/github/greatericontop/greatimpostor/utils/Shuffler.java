package io.github.greatericontop.greatimpostor.utils;

import java.util.Random;

public class Shuffler {

    public static void shuffle(Object[] arr, Random random) {
        // Durstenfeld Shuffle
        for (int i = arr.length-1; i >= 1; i--) {
            int other = random.nextInt(i+1); // 0 to i, inclusive
            Object temp = arr[other];
            arr[other] = arr[i];
            arr[i] = temp;
        }
    }

}
