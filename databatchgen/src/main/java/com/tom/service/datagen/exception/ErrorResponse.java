package com.tom.service.datagen.exception;

import java.util.Map;

public record ErrorResponse(Map<String, String> errors) {
}
