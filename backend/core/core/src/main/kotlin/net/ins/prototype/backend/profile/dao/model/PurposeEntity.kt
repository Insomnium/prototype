package net.ins.prototype.backend.profile.dao.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "purposes")
class PurposeEntity(
    @Id
    @Column(name = "id_purpose")
    var id: Long? = null,
    @Column(name = "mask")
    val mask: Int,
)
