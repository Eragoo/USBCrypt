package com.Erag0.USBCrypt.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InputDataLengthException extends RuntimeException{
    public InputDataLengthException(String message) {
        super(message);
    }
}
