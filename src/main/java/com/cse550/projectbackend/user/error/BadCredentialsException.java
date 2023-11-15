package com.cse550.projectbackend.user.error;

public class BadCredentialsException extends RuntimeException{
    public  BadCredentialsException(String s) {
        super(s);
    }
}
