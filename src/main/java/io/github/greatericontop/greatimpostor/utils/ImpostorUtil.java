package io.github.greatericontop.greatimpostor.utils;

public class ImpostorUtil {

    public static boolean checkOrthoInvSlots(int a, int b) {
        if (a != b+1 && a != b-1 && a != b+9 && a != b-9)  return false;
        if (a == b+1 && b % 9 == 8)  return false; // going to the right would mean crossing a row
        if (a == b-1 && b % 9 == 0)  return false; // going to the left would mean crossing a row
        return true;
    }

}
