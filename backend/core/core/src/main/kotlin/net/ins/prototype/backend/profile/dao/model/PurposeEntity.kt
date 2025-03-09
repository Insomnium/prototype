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
    @Column(name = "dating")
    val dating: Boolean,
    @Column(name = "sexting")
    val sexting: Boolean,
    @Column(name = "relationships")
    val relationships: Boolean,
)
