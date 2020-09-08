package com.game.net;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public class NameableThreadFactory implements ThreadFactory{
    private static int threadsNum=0;
    private final String namePattern;

    public NameableThreadFactory(String baseName){
        namePattern = baseName + "-%d";
    }

    @Override
    public Thread newThread(@NotNull Runnable runnable){
        threadsNum++;
        return new Thread(runnable, String.format(namePattern, threadsNum));
    }
}