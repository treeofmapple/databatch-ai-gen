package com.tom.service.datagen.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

public class CustomBanner implements Banner {

	@Override
	public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
		getPathResource(out);
		try {
			Package springPackage = SpringBootVersion.class.getPackage();
			String version = (springPackage != null) ? springPackage.getImplementationVersion() : "unknown";
            String appName = environment.getProperty("spring.application.name", "Unnamed App");
            boolean sslEnabled = Boolean.parseBoolean(environment.getProperty("server.ssl.enabled", "false"));
            String serverPort = environment.getProperty("server.port", "8080");
            String profiles = String.join(", ", environment.getActiveProfiles());
            String protocol = sslEnabled ? "https" : "http";
            String ip = getPublicIp();
            
            out.println();
            out.println();
            out.println("Powered by Spring Boot: " + version);
            out.println("APP: " + appName);
            out.println("Active Profile: " + profiles);
            out.println("====================================================================================");
            out.printf("Running at: %s://%s:%s%n", protocol, ip, serverPort);
            out.println("====================================================================================");
        } catch (Exception e) {
            out.println("Failed to print custom banner: " + e.getMessage());
        }
    }
	
	private String getPublicIp() {
	    String publicIp = "Unknown";
	    try {
	        @SuppressWarnings("deprecation")
			URL url = new URL("https://ifconfig.me");
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");
	        connection.setRequestProperty("User-Agent", "curl");

	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
	            publicIp = reader.readLine();
	        }
	    } catch (IOException e) {
	        ServiceLogger.error(e.getMessage());
	    }
	    return publicIp;
	}
	
	private void getPathResource(PrintStream out) {
		ClassPathResource resource = new ClassPathResource("banner/banner.txt");
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
