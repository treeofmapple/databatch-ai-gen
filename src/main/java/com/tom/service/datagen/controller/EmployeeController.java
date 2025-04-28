package com.tom.service.datagen.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tom.service.datagen.common.ConnectionUtil;
import com.tom.service.datagen.common.GenerateData;
import com.tom.service.datagen.common.ServiceLogger;
import com.tom.service.datagen.dto.RandomRequest;
import com.tom.service.datagen.service.EmployeeService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "GenerateEmployeeData", description = "Batch of employee Data")
public class EmployeeController extends ConnectionUtil {

	private final EmployeeService service;
	private final GenerateData data;

	@PostMapping(value = "/employee/progress/{quantity}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> dataGenerationWithProgress(@PathVariable int quantity) {
		return service.generateEmployeeDataWithProgress(quantity).map(data -> "Progress: " + data)
				.concatWith(Mono.just("Completed. Download your file at: /api/v1/employee/download/"));
	}

	@GetMapping(value = "/employee/download/{fileId}", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<byte[]> downloadEmployeeData(@PathVariable String fileId) {
		byte[] csvData = service.retrieveCsvFromTempStorage(fileId);
		if (csvData == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found or expired.".getBytes());
		}
		return buildCsvResponse(csvData, "employees.csv");
	}

	@DeleteMapping("/employee/delete/{fileId}")
	public ResponseEntity<String> deleteEmployeeData(@PathVariable String fileId) {
		ServiceLogger.info("Deleting Data from Storage");
		service.deleteCsvFromTempStorage(fileId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted: " + fileId);
	}

	@PostMapping(value = "/employee/{quantity}", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<byte[]> dataGeneration(@PathVariable int quantity, HttpServletRequest request) {
		byte[] csvData = service.generateEmployeeData(quantity, request);
		return buildCsvResponse(csvData, "employees.csv");

	}

	@PostMapping(value = "/employee/batch/small", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<byte[]> dataSmallGeneration(HttpServletRequest request) {
		final int quantity = 100;
		byte[] csvData = service.generateEmployeeData(quantity, request);
		return buildCsvResponse(csvData, "employees.csv");
	}

	@PostMapping(value = "/insert", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> insertAtributes(@RequestBody @Valid RandomRequest request) {
		data.setVariables(request);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("Inserted Values");
	}

}
