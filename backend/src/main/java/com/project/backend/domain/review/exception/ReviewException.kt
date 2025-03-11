package com.project.backend.domain.review.exception

import org.springframework.http.HttpStatus

data class ReviewException (
    val status: HttpStatus,
    val errorCode: String,
    override  val message: String
) : RuntimeException(message){
    constructor(errorCode: ReviewErrorCode) : this(
        status = errorCode.status,
        errorCode = errorCode.errorCode,
        message = errorCode.message
    )
}