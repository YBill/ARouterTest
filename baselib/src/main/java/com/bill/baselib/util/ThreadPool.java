package com.bill.baselib.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

/**
 * Created by Bill on 2022/4/3.
 */

public class ThreadPool {

    private static class SingletonHolder {
        private final static ThreadPool instance = new ThreadPool();
    }

    public static ThreadPool getInstance() {
        return SingletonHolder.instance;
    }

    private final Executor netThread;
    private final MainThreadExecutor mainThread;

    public ThreadPool() {
        this.netThread = Executors.newSingleThreadExecutor();
        this.mainThread = new MainThreadExecutor();
    }

    public void executeMain(Runnable runnable) {
        this.mainThread.execute(runnable);
    }

    public void executeMain(Runnable runnable, long time) {
        this.mainThread.execute(runnable, time);
    }

    public void executeThread(Runnable runnable) {
        this.netThread.execute(runnable);
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler;

        private MainThreadExecutor() {
            this.mainThreadHandler = new Handler(Looper.getMainLooper());
        }

        public void execute(@NonNull Runnable command) {
            this.mainThreadHandler.post(command);
        }

        public void execute(Runnable command, long time) {
            this.mainThreadHandler.postDelayed(command, time);
        }
    }

}
