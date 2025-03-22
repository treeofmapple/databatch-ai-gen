package com.tom.service.datagen.model;

import java.time.LocalDate;

import com.tom.service.datagen.model.enums.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private int age;
	private String phoneNumber;
	private Gender gender;
	private String department;
	private String jobTitle;
	private double salary;
	private int yearsOfExperience;
	private String address;
	private LocalDate hireDate;
	private boolean active;
	private LocalDate terminationDate;
}
