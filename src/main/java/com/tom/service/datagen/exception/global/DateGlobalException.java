package com.tom.service.datagen.exception.global;

@SuppressWarnings("serial")
public abstract class DateGlobalException extends NumberFormatException {
	
	private final Throwable cause;
	
	public DateGlobalException(String msg) {
        super(msg);
        this.cause = null;
    }

    public DateGlobalException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }
    
    @Override
    public synchronized Throwable getCause() {
        return cause;
    }
}
