package com.tom.service.datagen.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tom.service.datagen.model.Employee;
import com.tom.service.datagen.model.enums.Gender;

@Component
public class Operations {

	private static final String CSV_HEADER = String.join(",",
			"ID", "First Name", "Last Name", "Email", "Phone", "Age",
			"Gender", "Department", "Job Title", "Salary", "Experience",
			"Address", "Hire Date", "Termination Date"
	);
	
	public String generateRandomUUID() {
		return UUID.randomUUID().toString();
	}
	
	public void logProgress(int current, int total) {
		int barSize = 20;
		int progress = (int) (((double) current / total) * barSize);
		String bar = "[" + "=".repeat(progress) + " ".repeat(barSize - progress) + "]";
		ServiceLogger.info("Batch Progress: {} {}/{}", bar, current, total);
	}

	public byte[] convertToCSV(List<Employee> employees) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
		     PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {

			writer.println(CSV_HEADER);

			for (Employee emp : employees) {
				writer.println(buildCsvRow(emp));
			}

			writer.flush();
			return out.toByteArray();
		} catch (Exception e) {
			ServiceLogger.error("Error generating CSV", e);
			return new byte[0];
		}
	}
	
	private String buildCsvRow(Employee emp) {
		return String.format("%s,%s,%s,%s,%s,%d,%s,%s,%s,%.2f,%d,%s,%s,%s",
				nullSafe(emp.getId()),
				nullSafe(emp.getFirstName()),
				nullSafe(emp.getLastName()),
				nullSafe(emp.getEmail()),
				nullSafe(emp.getPhoneNumber()),
				nullSafe(emp.getAge()),
				nullSafe(emp.getGender()),
				nullSafe(emp.getDepartment()),
				nullSafe(emp.getJobTitle()),
				nullSafe(emp.getSalary()),
				nullSafe(emp.getYearsOfExperience()),
				nullSafe(emp.getAddress()),
				nullSafe(emp.getHireDate()),
				nullSafe(emp.getTerminationDate())
		);
	}

	private String nullSafe(double value) {
		return value != 0.0 ? String.format("%.2f", value) : "";
	}
	
	private String nullSafe(int value) {
	    return value != 0 ? Integer.toString(value) : "";
	}
	
	private String nullSafe(Long value) {
		return value != null ? value.toString() : "";
	}

	private String nullSafe(String value) {
		return value != null ? value : "";
	}

	private String nullSafe(Gender gender) {
		return gender != null ? gender.name() : "";
	}

	private String nullSafe(LocalDate date) {
		return date != null ? date.toString() : "";
	}
}
