package com.tom.service.datagen.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class WhitelistLoader {

    public String[] loadWhitelist() throws IOException {
        List<String> paths = new ArrayList<>();
        var resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:/whitelist/*.txt");
        
        for (Resource resource : resources) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                paths.addAll(reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .collect(Collectors.toList()));
            }
        }

        return paths.toArray(new String[0]);
    }
}