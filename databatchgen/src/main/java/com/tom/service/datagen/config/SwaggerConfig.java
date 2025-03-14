package com.tom.service.datagen.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
	    info = @Info(
	        title = "Url Shortener",
	        description = "Encurtador de Urls",
	        version = "v1.11a",
	        contact = @Contact(
	            name = "Samuel",
	            url = "https://"
	        )
	    ),
	    servers = {
	        @Server(url = "http://localhost:8000", description = "Servidor de Desenvolvimento"),
	        @Server(url = "http://localhost:8005", description = "Servidor de Produção")
	    }
	)
public class SwaggerConfig {
	
}