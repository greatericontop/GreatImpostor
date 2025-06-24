package io.github.greatericontop.greatimpostor.utils;

/*
 * Copyright (C) 2023-present greateric.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty  of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
