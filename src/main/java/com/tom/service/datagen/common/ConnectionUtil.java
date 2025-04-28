package com.tom.service.datagen.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
@Component
public class ConnectionUtil {
	
	public boolean isClientConnected(HttpServletRequest request) {
		try {
			if (!request.isAsyncStarted() && request.getInputStream().available() == 0) {
				ServiceLogger.warn("Client aborted the request before processing");
				return false;
			}
		} catch (Exception e) {
			ServiceLogger.error("Error checking client connection", e);
			return false;
		}
		return true;
	}

	public ResponseEntity<byte[]> buildCsvResponse(byte[] csvData, String filename) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

		return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(csvData);
	}
}
