package org.example.exceptions;

public class MethodBodyNotFoundException extends IllegalStateException {
    public MethodBodyNotFoundException(String s) {
        super(s);
    }
}
