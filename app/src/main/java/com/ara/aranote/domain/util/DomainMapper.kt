package com.ara.aranote.domain.util

interface DomainMapper<T, DomainEntity> {

    fun mapToDomainEntity(model: T): DomainEntity

    fun mapFromDomainEntity(domainEntity: DomainEntity): T

    fun toDomainList(lstModel: List<T>): List<DomainEntity>

    fun fromDomainList(lstDomainEntity: List<DomainEntity>): List<T>
}
