package me.yvesz.jadxServer.model

data class ServiceResponse<T>(
    val success: Boolean,
    val error: String? = null,
    val data: T? = null
)
    
