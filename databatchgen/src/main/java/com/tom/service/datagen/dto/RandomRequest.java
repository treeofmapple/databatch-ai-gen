package com.tom.service.datagen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RandomRequest(

        @Min(value = 1, message = "Gender must be at least 1")
        @Max(value = 100, message = "Gender cannot exceed 100")
        @Schema(
                accessMode = Schema.AccessMode.AUTO, 
                type = "integer", 
                description = "Gender identifier", 
                example = "1")
        int gender,

        @Min(value = 1, message = "Age must be at least 1")
        @Max(value = 100, message = "Age cannot exceed 100")
        @Schema(
                accessMode = Schema.AccessMode.AUTO, 
                type = "integer", 
                description = "Age in years", 
                example = "25")
        int age,

        @Min(value = 1, message = "Experience must be at least 1")
        @Max(value = 100, message = "Experience cannot exceed 100")
        @Schema(
                accessMode = Schema.AccessMode.AUTO, 
                type = "integer", 
                description = "Years of experience", 
                example = "5")
        int experience,

        @Min(value = 1, message = "Salary must be at least 1")
        @Max(value = 100, message = "Salary cannot exceed 100")
        @Schema(
                accessMode = Schema.AccessMode.AUTO, 
                type = "integer", 
                description = "Salary level", 
                example = "50")
        int salary

) {
}
