package com.tom.service.datagen.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tom.service.datagen.common.DatagenUtil;
import com.tom.service.datagen.common.SystemCommon;
import com.tom.service.datagen.exception.DataProcessingException;
import com.tom.service.datagen.model.Employee;
import com.tom.service.datagen.model.enums.Gender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService extends SystemCommon implements DatagenUtil {

	@Value("${application.datagen.batchSize:10000}")
	private int batchSize;

	private Set<String> usedEmails = new HashSet<>();
	private Set<String> usedPhoneNumbers = new HashSet<>();

	public Flux<String> generateEmployeeDataWithProgress(int quantity) {
		if(!csvStorage.isEmpty()) {
			deleteCsvFromTempStorage(stored);
		}
		return Flux.create(sink -> {
			int totalBatches = (int) Math.ceil((double) quantity / batchSize);
			List<Employee> allEmployees = new ArrayList<>();
			long lastUpdateTime = System.currentTimeMillis();

			for (int i = 0; i < totalBatches; i++) {
				List<Employee> batch = new ArrayList<>(batchSize);

				for (int j = 0; j < batchSize && (i * batchSize + j) < quantity; j++) {
					Employee emp;
					int retries = 5;
					while (true) {
						try {
							emp = generateSingleEmployee();
							batch.add(emp);
							break;
						} catch (DataProcessingException e) {
							log.warn("Duplicate email detected, retrying... [{} retries left]", --retries);
							if (retries == 0) {
								sink.error(e);
								return;
							}
						}
					}
				}

				allEmployees.addAll(batch);
				sink.next("Batch " + (i + 1) + " of " + totalBatches + " processed.");

				if (System.currentTimeMillis() - lastUpdateTime >= 5000) {
					sink.next("Still processing... " + (i + 1) + "/" + totalBatches);
					lastUpdateTime = System.currentTimeMillis();
				}
			}

			byte[] csvData = convertToCSV(allEmployees);
			String fileId = saveCsvTemporarily(csvData);
			log.info("Sucessfull data download");
			sink.next("Completed. Download your file at: /api/v1/employee/download/" + fileId);
			sink.complete();
		});
	}

	public byte[] generateEmployeeData(int quantity) {
		
		if(!csvStorage.isEmpty()) {
			deleteCsvFromTempStorage(stored);
		}
		
		int totalBatches = (int) Math.ceil((double) quantity / batchSize);
		List<Employee> allEmployees = new ArrayList<>();

		for (int i = 0; i < totalBatches; i++) {
			List<Employee> batch = new ArrayList<>(batchSize);

			for (int j = 0; j < batchSize && (i * batchSize + j) < quantity; j++) {
				Employee emp;
				int retries = 5;
				while (true) {
					try {
						emp = generateSingleEmployee();
						batch.add(emp);
						break;
					} catch (DataProcessingException e) {
						log.warn("Duplicate email detected, retrying... [{} retries left]", --retries);
						if (retries == 0) {
							throw e;
						}
					}
				}
				if (j % 25 == 0 || j == batchSize - 1) {
					logProgress(batch.size(), batchSize);
				}
			}

			allEmployees.addAll(batch);
			log.info("Batch {} of {} saved (Size: {})", i + 1, totalBatches, batch.size());
		}

		var data = convertToCSV(allEmployees);
		saveCsvTemporarily(data);
		return data;
	}

	private byte[] convertToCSV(List<Employee> employees) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); PrintWriter writer = new PrintWriter(out)) {

			writer.println(
					"ID,First Name,Last Name,Email,Phone,Age,Gender,Department,Job Title,Salary,Experience,Address,Hire Date,Termination Date");

			for (Employee emp : employees) {
				writer.printf("%s,%s,%s,%s,%s,%d,%s,%s,%s,%.2f,%d,%s,%s,%s%n", emp.getId(), emp.getFirstName(),
						emp.getLastName(), emp.getEmail(), emp.getPhoneNumber(), emp.getAge(), emp.getGender(),
						emp.getDepartment(), emp.getJobTitle(), emp.getSalary(), emp.getYearsOfExperience(),
						emp.getAddress(), emp.getHireDate(),
						emp.getTerminationDate() != null ? emp.getTerminationDate() : "");
			}

			writer.flush();
			return out.toByteArray();
		} catch (Exception e) {
			log.error("Error generating CSV", e);
			return new byte[0];
		}
	}

	private Employee generateSingleEmployee() {
		Employee emp = new Employee();

		emp.setId(atomicCounter.incrementAndGet());

		boolean isMale = ThreadLocalRandom.current().nextInt(100) < gender;
		emp.setGender(isMale ? Gender.MALE : Gender.FEMALE);

		do {
			emp.setFirstName(isMale ? faker.name().malefirstName() : faker.name().femaleFirstName());

			emp.setEmail(faker.internet().safeEmailAddress());
			emp.setPhoneNumber(faker.phoneNumber().cellPhone());
		} while (usedEmails.contains(emp.getEmail()) || usedPhoneNumbers.contains(emp.getPhoneNumber()));

		emp.setLastName(faker.name().lastName());
		emp.setDepartment(faker.company().industry());
		emp.setJobTitle(faker.job().title());
		emp.setAddress(faker.address().fullAddress());

		emp.setAge(getRandomNumber(isAtributesMet(age) ? 19 : 41, isAtributesMet(age) ? 41 : 59));

		emp.setYearsOfExperience(
				getRandomNumber(isAtributesMet(experience) ? 1 : 11, isAtributesMet(experience) ? 11 : 31));

		emp.setSalary(
				getRandomNumber(isAtributesMet(salary) ? 30000 : 400000, isAtributesMet(salary) ? 400001 : 700001));

		LocalDate hireDate = LocalDate.now().minusDays(getRandomNumber(1, 3650));
		emp.setHireDate(hireDate);

		boolean isTerminated = ThreadLocalRandom.current().nextInt(100) < 20;
		emp.setTerminationDate(isTerminated ? hireDate.plusDays(15) : null);

		return emp;
	}

}
