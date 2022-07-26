package me.lozm.global.model.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity extends BaseDateTimeEntity {

    @CreatedBy
    @Column(name = "CREATED_BY", updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    private Long lastModifiedBy;

    @Column(name = "IS_USE")
    protected Boolean isUse;


    public void updateIsUse(boolean isUse) {
        this.isUse = isUse;
    }

}
