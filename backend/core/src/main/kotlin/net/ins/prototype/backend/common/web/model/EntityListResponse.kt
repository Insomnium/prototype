package net.ins.prototype.backend.common.web.model

data class EntityListResponse<T : Any>(
    val results: List<T>,
)
