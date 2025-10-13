package com.example.reflection;

import java.util.ArrayList;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import java.util.Map;

@Schema(name = "SampleModel", description = "Sample data model")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "number", "text", "nested", "myLinkedList", "status", "mapField", "doubleArray" })
public class Sample {
    @Schema(description = "An integer number", example = "123")
    public int number;
    @Schema(description = "A text string", example = "example")
    public String text;
    @Schema(description = "Nested object")
    public Nested nested;
    @Schema(description = "Custom linked list")
    public MyLinkedList myLinkedList;

    @Schema(description = "Status enum", required = true)
    @JsonProperty("status")
    public Status status;

    @Schema(description = "Map of string to integer", type = "object") // additionalProperties defaults to mapping values
    public Map<String, Integer> mapField;

    @ArraySchema(schema = @Schema(description = "Array of doubles", type = "number", format = "double"),
                 minItems = 1, maxItems = 5)
    public double[] doubleArray;

    public static class Nested {
        @Schema(name = "NestedModel", description = "Nested class in Sample")
        public double value;
        @Schema(description = "Leaf object")
        public Leaf leaf;

        @Schema(description = "List of strings")
        public ArrayList<String> listOfStrings;
    }

    public static class Leaf {
        @Schema(name = "LeafModel", description = "Leaf class")
        public boolean flag;
    }

    @Schema(name = "MyLinkedListModel", description = "Custom linked list class")
    public class MyLinkedList {
        @Schema(description = "Head of the linked list")
        public Node head;

        public class Node {
            @Schema(description = "Data in node", example = "42")
            public int data;
            @Schema(description = "Next node")
            public Node next;
        }
    }

    // Enum demonstrating serialization/deserialization
    @Schema(description = "Example status enum")
    public enum Status {
        @Schema(description = "Active status") ACTIVE("active"),
        @Schema(description = "Inactive status") INACTIVE("inactive");

        private final String value;

        Status(String value) { this.value = value; }

        @JsonValue
        public String getValue() { return value; }

        @JsonCreator
        public static Status fromValue(String value) {
            for (Status s : Status.values()) {
                if (s.value.equals(value)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + value);
        }
    }
}
