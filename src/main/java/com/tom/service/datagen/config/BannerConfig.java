package com.tom.service.datagen.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;

import com.tom.service.datagen.common.CustomBanner;

public class BannerConfig implements SpringApplicationRunListener {
    
    @SuppressWarnings("unused")
	private final SpringApplication application;
    
    public BannerConfig(SpringApplication application, String[] args) {
        this.application = application;
        application.setBanner(new CustomBanner());
    }

}