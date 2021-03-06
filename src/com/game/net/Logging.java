package com.game.net;

import com.game.net.packets.Packet;

public class Logging {

    @SuppressWarnings("UnusedReturnValue")
    public static void log(Object o) {
        if (o == null) o = "None";
        _log(o.toString());
    }


    @SuppressWarnings({"unused", "UnusedReturnValue", "SameParameterValue"})
    public static void log(String fmt, Object... formatData) {
        Object[] fwdAray = new Object[formatData.length];
        for (int i = 0; i < formatData.length; i++) {
            Object o;
            if (formatData[i] == null) {
                //Handle null objects
                o = "None";
            } else if (formatData[i].getClass().isArray()) {
                //Handle arrays
                Object[] arr = (Object[]) formatData[i];
                Class componenetType = arr.getClass().getComponentType();
                StringBuilder sb = new StringBuilder();
                sb.append(componenetType.getName()).append('[');

                if (componenetType == byte.class) {
                    //Byte array handling
                    byte[] newByteArray = new byte[arr.length];
                    for (int i1 = 0, arrLength = arr.length; i1 < arrLength; i1++) {
                        newByteArray[i1] = (Byte) arr[i1];
                    }
                    sb.append(Packet.getByteHexStr(newByteArray));
                } else {
                    //Other array handling
                    if (arr.length > 0)
                        sb.append(arr[0]);
                    for (int j = 1; j < arr.length; j++) {
                        sb.append(", ").append(arr[j]);
                    }
                }
                o = sb.toString();
            } else {
                o = formatData[i];
            }

            fwdAray[i] = o;
        }
        _log(String.format(fmt, fwdAray));

    }

    private static synchronized void _log(String message) {
        String originCls = new Exception().getStackTrace()[2].getClassName();
        String origin = String.format("%s %10s", originCls, Thread.currentThread().getName());
        String logMsg = String.format("%-7s: %s", origin, message);
        System.out.println(logMsg);
        //String logMsg = String.format("%-7s [%s:%-5s]: %s", "SERVER", this.socket.getInetAddress().getHostAddress(), this.socket.getPort(), message);
    }

    private static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }
}
