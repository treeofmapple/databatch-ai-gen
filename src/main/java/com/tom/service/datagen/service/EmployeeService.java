package com.tom.service.datagen.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tom.service.datagen.common.ConnectionUtil;
import com.tom.service.datagen.common.GenerateData;
import com.tom.service.datagen.common.Operations;
import com.tom.service.datagen.common.ServiceLogger;
import com.tom.service.datagen.exception.ClientDisconnectedException;
import com.tom.service.datagen.exception.InternalException;
import com.tom.service.datagen.model.Employee;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class EmployeeService {

	private final Map<String, byte[]> csvStorage = new ConcurrentHashMap<>();

	@Value("${application.datagen.batchSize:10000}")
	private int batchSize;

	private final ConnectionUtil connection;
	private final Operations operations;
	private final GenerateData data;
	private String lastStoredId;

	public Flux<String> generateEmployeeDataWithProgress(int quantity) {
		return Flux.create(sink -> {
			AtomicInteger progress = new AtomicInteger(0);

			Flux.range(0, (int) Math.ceil((double) quantity / batchSize)).flatMap(i -> {
				List<Employee> batch = new ArrayList<>(batchSize);
				for (int j = 0; j < batchSize && (i * batchSize + j) < quantity; j++) {
					batch.add(data.generateSingleEmployee());
				}
				return Flux.just(batch);
			}).doOnNext(batch -> {
				byte[] csvData = operations.convertToCSV(batch);
				String batchId = operations.generateRandomUUID();
				csvStorage.put(batchId, csvData);
				progress.addAndGet(batch.size());
				sink.next("Progress: " + progress.get() + "/" + quantity);
			}).doOnComplete(() -> {
				sink.complete();
				ServiceLogger.info("Completed data generation.");
			}).subscribe();
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
			ServiceLogger.error("Error generating employee data", e);
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
				Employee emp = data.generateSingleEmployee();
				batch.add(emp);
			}
			if (!batch.isEmpty()) {
				operations.logProgress(i * batchSize + batch.size(), quantity);
			}

			callback.onBatchComplete(batch, i + 1, totalBatches);
			allEmployees.addAll(batch);

			byte[] csvData = operations.convertToCSV(batch);
			String batchId = operations.generateRandomUUID();
			csvStorage.put(batchId, csvData);
		}

		return allEmployees;
	}

	@FunctionalInterface
	private interface BatchCallback {
		void onBatchComplete(List<Employee> batch, int batchNum, int totalBatches);
	}

}
