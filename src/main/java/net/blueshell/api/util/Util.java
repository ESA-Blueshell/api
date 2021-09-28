package net.blueshell.api.util;

public class Util {

    public static String getCallerMethod(int stackFramePosition) {
        var stackFrames = Thread.currentThread().getStackTrace();
        var caller = "Could not find caller?";
        if (stackFrames != null && stackFrames.length > stackFramePosition) {
            caller = stackFrames[stackFramePosition].toString();
        }
        return caller;
    }

}
