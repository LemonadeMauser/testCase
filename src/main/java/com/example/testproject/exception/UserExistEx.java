package com.example.testproject.exception;

public class UserExistEx extends RuntimeException {
    public UserExistEx(String message) {
        super(message);
    }
}
