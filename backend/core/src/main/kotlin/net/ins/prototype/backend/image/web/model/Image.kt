package net.ins.prototype.backend.image.web.model

data class Image(
    val id: Long,
    val cdnUri: String,
    val primary: Boolean,
)
