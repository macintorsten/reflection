package com.example.reflection;

import java.util.ArrayList;

public class Sample {
    public int number;
    public String text;
    public Nested nested;
    public MyLinkedList myLinkedList;

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
}
