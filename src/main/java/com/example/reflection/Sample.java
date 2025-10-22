package com.example.reflection;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Transient;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "number", "text", "nested", "myLinkedList", "status", "mapField", "doubleArray" })
public class Sample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public int number;
    public String text;
    @Transient
    public Nested nested;
    @Transient
    public MyLinkedList myLinkedList;

    @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    public Status status;

    @Transient
    public Map<String, Integer> mapField;

    @Transient
    public double[] doubleArray;

    public static class Nested {
        public double value;
        public Leaf leaf;
        public ArrayList<String> listOfStrings;
    }

    public static class Leaf {
        public boolean flag;
    }

    public class MyLinkedList {
        public Node head;

        public class Node {
            public int data;
            public Node next;
        }
    }

    public enum Status {
        ACTIVE("active"),
        INACTIVE("inactive");

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
