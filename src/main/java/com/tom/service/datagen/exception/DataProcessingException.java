package com.tom.service.datagen.exception;

import com.tom.service.datagen.exception.global.CustomGlobalException;

import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
public class DataProcessingException extends CustomGlobalException {

	public DataProcessingException(String msg) {
		super(msg);
	}

	public DataProcessingException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
