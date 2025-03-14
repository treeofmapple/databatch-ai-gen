package com.tom.service.datagen.exception;

@SuppressWarnings("serial")
public abstract class CustomGlobalException extends RuntimeException {
	public abstract String getMsg();
}
