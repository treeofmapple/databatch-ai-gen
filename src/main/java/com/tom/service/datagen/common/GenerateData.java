package com.tom.service.datagen.common;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.tom.service.datagen.dto.RandomRequest;
import com.tom.service.datagen.model.Employee;
import com.tom.service.datagen.model.enums.Gender;

import net.datafaker.Faker;

@Component
public class GenerateData {

    private final Set<String> usedEmails = ConcurrentHashMap.newKeySet();
    private final Set<String> usedPhoneNumbers = ConcurrentHashMap.newKeySet();
	Faker faker = new Faker();
	AtomicLong atomicCounter = new AtomicLong(0);
	ThreadLocalRandom loc = ThreadLocalRandom.current();
	
    private int gender, age, experience, salary;
    
	public Employee generateSingleEmployee() {
		Employee emp = new Employee();
		emp.setId(atomicCounter.incrementAndGet());
		boolean isMale = ThreadLocalRandom.current().nextInt(100) < gender;
		emp.setGender(isMale ? Gender.MALE : Gender.FEMALE);

		do {
			emp.setFirstName(isMale ? faker.name().malefirstName() : faker.name().femaleFirstName());

			emp.setEmail(faker.internet().safeEmailAddress());
			emp.setPhoneNumber(faker.phoneNumber().cellPhone());
		} while (usedEmails.contains(emp.getEmail()) || usedPhoneNumbers.contains(emp.getPhoneNumber()));

	    usedEmails.add(emp.getEmail());
	    usedPhoneNumbers.add(emp.getPhoneNumber());
		
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
	    emp.setTerminationDate(ThreadLocalRandom.current().nextInt(100) < 20 ? hireDate.plusDays(15) : null);
		return emp;
	}
	
	public void setVariables(RandomRequest request) {
		ServiceLogger.info("Inserting values of variables || Gender: {}, Age: {}, Experience: {}, Salary: {}", request.gender(),
				request.age(), request.experience(), request.salary());
		gender = request.gender();
		age = request.age();
		experience = request.experience();
		salary = request.salary();
	}
	
	private int getRandomNumber(int min, int max) {
	    if (max <= min) {
	        return min;
	    }
	    return loc.nextInt(max - min) + min;
	}

	private boolean isAtributesMet(int atribute) {
		return loc.nextInt(100) < atribute;
	} 
	
}
