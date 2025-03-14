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
import com.tom.service.datagen.dto.RandomRequest;
import com.tom.service.datagen.exception.ClientDisconnectedException;
import com.tom.service.datagen.exception.InternalException;
import com.tom.service.datagen.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
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

	@PostMapping(value = "/employee/progress/{quantity}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> dataGenerationWithProgress(@PathVariable int quantity) {
		log.info("Started to generate: {} employees", quantity);

	    return service.generateEmployeeDataWithProgress(quantity)
                .map(data -> "Progress: " + data)
                .concatWith(Mono.just("Completed. Download your file at: /api/v1/employee/download/"));
	}

	@GetMapping(value = "/employee/download/{fileId}", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<byte[]> downloadEmployeeData(@PathVariable String fileId) {
		log.info("Downloading data");
		byte[] csvData = service.retrieveCsvFromTempStorage(fileId);

		if (csvData == null) {
			log.warn("Wasn't possible to download the data");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found or expired.".getBytes());
		}
		log.info("Sucessfull data download");
		return buildCsvResponse(csvData, "employees.csv");
	}

	@DeleteMapping("/employee/delete/{fileId}")
	public ResponseEntity<String> deleteEmployeeData(@PathVariable String fileId) {
		log.info("Deleting Data from Storage");
		service.deleteCsvFromTempStorage(fileId);
		log.info("Sucessfull data deletion from Storage");
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted: " + fileId);
	}

	@PostMapping(value = "/employee/{quantity}", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<byte[]> dataGeneration(@PathVariable int quantity, HttpServletRequest request) {
		log.info("Started to generate: {}, employee's", quantity);

		try {
			if (isClientConnected(request)) {
				throw new ClientDisconnectedException("Client disconnected during data generation");
			}
			byte[] csvData = service.generateEmployeeData(quantity);
			log.info("Finished to generate: {}, employee's", quantity);
			return buildCsvResponse(csvData, "employees.csv");
		} catch (Exception e) {
			throw new InternalException("System internal Error");
		}

	}

	@PostMapping(value = "/employee/batch/small", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<byte[]> dataSmallGeneration(HttpServletRequest request) {
		final int quantity = 100;
		log.info("Started to generate: {}, employee's", quantity);

		try {
			if (isClientConnected(request)) {
				throw new ClientDisconnectedException("Client disconnected during data generation");
			}
			byte[] csvData = service.generateEmployeeData(quantity);
			log.info("Finished to generate: {}, employee's", quantity);
			return buildCsvResponse(csvData, "employees.csv");
		} catch (Exception e) {
			throw new InternalException("System internal Error");
		}

	}

	@PostMapping(value = "/employee/batch/medium", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<byte[]> dataMediumGeneration(HttpServletRequest request) {
		final int quantity = 1000;
		log.info("Started to generate: {}, employee's", quantity);

		try {
			if (isClientConnected(request)) {
				throw new ClientDisconnectedException("Client disconnected during data generation");
			}
			byte[] csvData = service.generateEmployeeData(quantity);
			log.info("Finished to generate: {}, employee's", quantity);
			return buildCsvResponse(csvData, "employees.csv");
		} catch (Exception e) {
			throw new InternalException("System internal Error");
		}
	}

	@PostMapping(value = "/employee/batch/big", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<byte[]> dataBigGeneration(HttpServletRequest request) {
		final int quantity = 10000;
		log.info("Started to generate: {}, employee's", quantity);

		try {
			if (isClientConnected(request)) {
				throw new ClientDisconnectedException("Client disconnected during data generation");
			}
			byte[] csvData = service.generateEmployeeData(quantity);
			log.info("Finished to generate: {}, employee's", quantity);
			return buildCsvResponse(csvData, "employees.csv");
		} catch (Exception e) {
			throw new InternalException("System internal Error");
		}

	}
	
	@Operation(summary = "Insert Employee Attributes", description = "Receives a JSON request to generate employee data.")
	@PostMapping(value = "/insert", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> insertAtributes(@RequestBody @Valid RandomRequest request, HttpServletRequest webRequest) {
		log.info("Inserting values of variables || Gender: {}, Age: {}, Experience: {}, Salary: {}", request.gender(),
				request.age(), request.experience(), request.salary());
		service.setVariables(request);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("Inserted Values");
	}

}
