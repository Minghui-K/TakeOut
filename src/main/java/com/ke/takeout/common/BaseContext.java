package com.ke.takeout.common;

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getId() {
        return threadLocal.get();
    }
}
