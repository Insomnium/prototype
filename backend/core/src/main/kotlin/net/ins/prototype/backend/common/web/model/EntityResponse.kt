package net.ins.prototype.backend.common.web.model

data class EntityResponse<T : Any>(
    val result: T,
)
