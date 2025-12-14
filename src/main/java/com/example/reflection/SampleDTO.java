package com.example.reflection;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import io.swagger.v3.oas.annotations.media.Schema;

public class SampleDTO {

    @Schema(description = "Text value", example = "Hello World")
    @NotNull
    @Size(min = 3, max = 100)
    public String text;

    @Schema(description = "Number value", example = "42")
    @NotNull
    @Min(0)
    @Max(1000)
    public Integer number;

    @Schema(description = "Status enum", example = "active")
    @NotNull
    public Sample.Status status;

    @Schema(description = "Extra key-value pairs", example = "{\"foo\":\"1\"}")
    public java.util.Map<String, String> extras;

}