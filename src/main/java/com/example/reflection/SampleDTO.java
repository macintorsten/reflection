package com.example.reflection;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class SampleDTO {

    @NotNull
    @Size(min = 3, max = 100)
    public String text;

    @NotNull
    @Min(0)
    @Max(1000)
    public Integer number;

    @NotNull
    public Sample.Status status;

    public java.util.Map<String, String> extras;

}