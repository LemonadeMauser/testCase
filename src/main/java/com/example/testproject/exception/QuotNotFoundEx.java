package com.example.testproject.exception;

public class QuotNotFoundEx extends RuntimeException {
    public QuotNotFoundEx(String s) {
        super(s);
    }
}
