package com.project.backend.domain.challenge.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;



@Getter
public class ChallengeException extends RuntimeException{
    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    public ChallengeException(HttpStatus status, String errorCode, String message){
        super(message);
        this.status=status;
        this.errorCode = errorCode;
        this.message=message;
    }
}
