package com.tom.service.datagen.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = true)
@Data
public class DataProcessingException extends CustomGlobalException {

	private final String msg;
}
