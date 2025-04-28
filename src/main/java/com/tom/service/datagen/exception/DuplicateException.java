package com.tom.service.datagen.exception;

import com.tom.service.datagen.exception.global.CustomGlobalException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class DuplicateException extends CustomGlobalException {
	
	public DuplicateException(String msg) {
		super(msg);
	}
	
	public DuplicateException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
