/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.util;

public class AppException extends RuntimeException {
    private final String code;
    private final String userMessage;

    public AppException(String code, String userMessage) {
        super(userMessage);
        this.code = code;
        this.userMessage = userMessage;
    }
    public AppException(String code, String userMessage, Throwable cause) {
        super(userMessage, cause);
        this.code = code;
        this.userMessage = userMessage;
    }
    public String getCode() { return code; }
    public String getUserMessage() { return userMessage; }
}
