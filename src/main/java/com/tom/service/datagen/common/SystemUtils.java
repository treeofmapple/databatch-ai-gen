package com.tom.service.datagen.common;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SystemUtils {

	public String getUserIp() {
	    var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
	        .getRequest();
	    return request.getRemoteAddr();
	}
}
