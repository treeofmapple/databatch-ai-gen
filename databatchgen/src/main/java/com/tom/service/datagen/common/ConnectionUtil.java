package com.tom.service.datagen.common;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionUtil {
	protected boolean isClientConnected(HttpServletRequest request) {
		try {
			if (!request.isAsyncStarted() && request.getInputStream().available() == 0) {
				log.warn("Client aborted the request before processing");
				return false;
			}
		} catch (Exception e) {
			log.error("Error checking client connection", e);
			return false;
		}
		return true;
	}

	protected ResponseEntity<byte[]> buildCsvResponse(byte[] csvData, String filename) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

		return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(csvData);
	}
}
