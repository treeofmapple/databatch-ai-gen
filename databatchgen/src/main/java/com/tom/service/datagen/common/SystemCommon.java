package com.tom.service.datagen.common;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.tom.service.datagen.dto.RandomRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SystemCommon implements DatagenUtil {

	protected final Map<String, byte[]> csvStorage = new ConcurrentHashMap<>();
	protected String stored;
	protected int gender;
	protected int age;
	protected int experience;
	protected int salary;
	
	protected void logProgress(int current, int total) {
		int barSize = 20;
		int progress = (int) (((double) current / total) * barSize);
		String bar = "[" + "=".repeat(progress) + " ".repeat(barSize - progress) + "]";
		log.info("Batch Progress: {} {}/{}", bar, current, total);
	}
	
	protected String generateRandomUUID() {
		String data = UUID.randomUUID().toString();
		stored = data;
		return data;
	}
	
	public String saveCsvTemporarily(byte[] csvData) {
	    csvStorage.put(generateRandomUUID(), csvData);
	    return generateRandomUUID();
	}

	public byte[] retrieveCsvFromTempStorage(String fileId) {
	    return csvStorage.get(fileId);
	}
	
	public byte[] deleteCsvFromTempStorage(String fileId) {
	    return csvStorage.remove(fileId);
	}

	protected static int getRandomNumber(int min, int max) {
	    if (max <= min) {
	        return min;
	    }
	    return loc.nextInt(max - min) + min;
	}


	protected static boolean isAtributesMet(int atribute) {
		return loc.nextInt(100) < atribute;
	} 
	
	public void setVariables(RandomRequest request) {
		gender = request.gender();
		age = request.age();
		experience = request.experience();
		salary = request.salary();
	}
}
