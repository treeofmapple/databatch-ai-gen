package com.tom.service.datagen.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceLogger {

    private static String getCallerClassName() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames
                        .skip(2)
                        .findFirst()
                        .map(frame -> frame.getDeclaringClass().getSimpleName())
                        .orElse("UnknownClass"));
    }

    public static void info(String message) {
        log.info("[{}] -> {}", getCallerClassName(), message);
    }

    public static void info(String message, Object... args) {
        log.info("[{}] -> " + message, getCallerClassName(), args);
    }
    
    public static void warn(String message) {
        log.warn("[{}] -> {}", getCallerClassName(), message);
    }

    public static void warn(String message, Object... args) {
        log.warn("[{}] -> " + message, getCallerClassName(), args);
    }
    
    public static void error(String message) {
        log.error("[{}] -> {}", getCallerClassName(), message);
    }
    
    public static void error(String message, Object... args) {
        log.error("[{}] -> " + message, getCallerClassName(), args);
    }

    public static void debug(String message) {
        log.debug("[{}] -> {}", getCallerClassName(), message);
    }
    
    public static void debug(String message, Object... args) {
        log.debug("[{}] -> " + message, getCallerClassName(), args);
    }
	
}
