//package com.project.backend.domain.notification.exception;
//
//
//import com.project.backend.global.response.GenericResponse;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice(basePackages = "com.project.backend.domain.notification")
//public class NotificationExceptionHandler {
//
//    @ExceptionHandler(NotificationException.class)
//    public ResponseEntity<GenericResponse<String>> handleNotificationException(NotificationException ex){
//        return ResponseEntity.status(ex.getStatus()).body(
//                GenericResponse.of(
//                        ex.getErrorCode(),
//                        ex.getMessage()
//                )
//        );
//    }
//}
