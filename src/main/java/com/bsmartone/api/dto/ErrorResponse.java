/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bsmartone.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ErrorResponse {
    private String code;       // p.ej. "VALIDATION_ERROR", "USER_INACTIVE"
    private String message;    // mensaje legible para el usuario
    private String requestId;  // id para rastrear en logs
    private String path;       // endpoint afectado
}
