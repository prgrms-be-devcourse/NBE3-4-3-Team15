package com.project.backend.global.baseEntity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseEntityK { // Kotlin에서는 JPA 상속을 위해 `open class` 사용

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime // lateinit 사용하여 JPA가 자동 설정 가능하도록 함

    @LastModifiedDate
    @Column(name = "modified_at")
    var modifiedAt: LocalDateTime? = null // nullable 처리하여 초기값이 없을 수 있도록 설정
}
