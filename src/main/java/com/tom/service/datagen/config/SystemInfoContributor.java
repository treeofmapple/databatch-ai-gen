package com.tom.service.datagen.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SystemInfoContributor implements InfoContributor {

    private final Environment environment;

	@Override
	public void contribute(Info.Builder builder) {
		Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("spring.application.name", environment.getProperty("spring.application.name"));
        systemInfo.put("project.version", environment.getProperty("project.version"));
		systemInfo.put("os.name", System.getProperty("os.name"));
		systemInfo.put("os.arch", System.getProperty("os.arch"));
		systemInfo.put("java.version", System.getProperty("java.version"));
		systemInfo.put("user.name", System.getProperty("user.name"));

		builder.withDetail("system", systemInfo);
	}
}
