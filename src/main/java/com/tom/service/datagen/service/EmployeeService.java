package com.tom.service.datagen.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tom.service.datagen.common.ConnectionUtil;
import com.tom.service.datagen.common.GenerateData;
import com.tom.service.datagen.common.Operations;
import com.tom.service.datagen.common.ServiceLogger;
import com.tom.service.datagen.exception.ClientDisconnectedException;
import com.tom.service.datagen.exception.DataProcessingException;
import com.tom.service.datagen.exception.InternalException;
import com.tom.service.datagen.model.Employee;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

	private final Map<String, byte[]> csvStorage = new ConcurrentHashMap<>();

	@Value("${application.datagen.batchSize:10000}")
	private int batchSize;

	private ConnectionUtil connection;
	private Operations operations;
	private GenerateData data;
	private String lastStoredId;

	public Flux<String> generateEmployeeDataWithProgress(int quantity) {
		ServiceLogger.info("Started to generate: {} employees", quantity);
		clearPreviousData();

		return Flux.create(sink -> {
			try {
				List<Employee> allEmployees = generateEmployees(quantity, (batch, batchNum, totalBatches) -> {
					sink.next("Batch " + batchNum + " of " + totalBatches + " processed.");
				});

				byte[] csvData = operations.convertToCSV(allEmployees);
				lastStoredId = operations.generateRandomUUID();
				csvStorage.put(lastStoredId, csvData);

				ServiceLogger.info("Successful data generation and storage.");
				sink.next("Completed. Download your file at: /api/v1/employee/download/" + lastStoredId);
				sink.complete();
			} catch (Exception e) {
				sink.error(e);
			}
		});
	}

	public byte[] generateEmployeeData(int quantity, HttpServletRequest request) {
		ServiceLogger.info("Started to generate: {} employees", quantity);

		try {
			if (connection.isClientConnected(request)) {
				throw new ClientDisconnectedException("Client disconnected during data generation");
			}

			clearPreviousData();
			List<Employee> allEmployees = generateEmployees(quantity, (batch, batchNum, totalBatches) -> {
				ServiceLogger.info("Batch {} of {} saved (Size: {})", batchNum, totalBatches, batch.size());
			});

			byte[] csvData = operations.convertToCSV(allEmployees);
			lastStoredId = operations.generateRandomUUID();
			csvStorage.put(lastStoredId, csvData);

			ServiceLogger.info("Finished generating {} employees", quantity);
			return csvData;
		} catch (Exception e) {
			throw new InternalException("System internal Error", e);
		}
	}
	
	public byte[] retrieveCsvFromTempStorage(String fileId) {
	    ServiceLogger.info("Attempting to download data for fileId: {}", fileId);
	    byte[] csvData = csvStorage.get(fileId);
	    if (csvData != null) {
	        ServiceLogger.info("Successful data retrieval for fileId: {}", fileId);
	    } else {
	        ServiceLogger.warn("Data not found or expired for fileId: {}", fileId);
	    }
	    return csvData;
	}
	
	public byte[] deleteCsvFromTempStorage(String fileId) {
	    ServiceLogger.info("Deleting Data from Storage");
	    byte[] removedData = csvStorage.remove(fileId);
	    if (removedData != null) {
	        ServiceLogger.info("Successful data deletion from Storage");
	    } else {
	        ServiceLogger.warn("No data found for deletion with fileId: {}", fileId);
	    }
	    return removedData;
	}

	private void clearPreviousData() {
		if (!csvStorage.isEmpty()) {
			csvStorage.remove(lastStoredId);
		}
	}
	
	private List<Employee> generateEmployees(int quantity, BatchCallback callback) {
		int totalBatches = (int) Math.ceil((double) quantity / batchSize);
		List<Employee> allEmployees = new ArrayList<>();

		for (int i = 0; i < totalBatches; i++) {
			List<Employee> batch = new ArrayList<>(batchSize);

			for (int j = 0; j < batchSize && (i * batchSize + j) < quantity; j++) {
				Employee emp;
				int retries = 5;
				while (true) {
					try {
						emp = data.generateSingleEmployee();
						batch.add(emp);
						break;
					} catch (DataProcessingException e) {
						ServiceLogger.warn("Duplicate email detected, retrying... [{} retries left]", --retries);
						if (retries == 0) {
							throw e;
						}
					}
				}
				if (j % 25 == 0 || j == batchSize - 1) {
					operations.logProgress(batch.size(), batchSize);
				}
			}

			allEmployees.addAll(batch);
			callback.onBatchComplete(batch, i + 1, totalBatches);
		}

		return allEmployees;
	}
	
	@FunctionalInterface
	private interface BatchCallback {
		void onBatchComplete(List<Employee> batch, int batchNum, int totalBatches);
	}

}
