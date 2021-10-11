package com.ara.aranote.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Notebook(
    val id: Int,
    val name: String,
)
