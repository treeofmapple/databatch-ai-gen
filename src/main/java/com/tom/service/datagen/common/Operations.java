package com.tom.service.datagen.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.tom.service.datagen.model.Employee;

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
        StringBuilder row = new StringBuilder();

        Field[] fields = Employee.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(emp);
                row.append(value).append(",");
            } catch (IllegalAccessException e) {
                ServiceLogger.error("Error accessing field: " + field.getName(), e);
                row.append(",");
            }
        }
        if (row.length() > 0) {
            row.setLength(row.length() - 1);
        }
        return row.toString();
    }

}
