package net.ins.prototype.backend.profile.dao.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PostLoad
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import net.ins.prototype.backend.profile.model.Gender
import net.ins.prototype.backend.profile.model.Purpose
import net.ins.prototype.backend.profile.model.calculateMask
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "profiles")
class ProfileEntity(
    @Id
    @Column(name = "id_profile")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "title")
    val title: String,
    @Column(name = "birth")
    val birth: LocalDate,
    @Column(name = "gender")
    var genderCode: Char,
    @Transient
    var gender: Gender,
    @Column(name = "country_id")
    val countryId: String,
    @Column(name = "purpose_mask")
    var purposeMask: Int,
    @Column(name = "created_at")
    var createdAt: LocalDateTime,
    @Column("last_indexed_at")
    var lastIndexedAt: LocalDateTime? = null,
    @Column(name = "id_index")
    var indexId: String? = null,
) {

    constructor(title: String, birth: LocalDate, gender: Gender, purposeMask: Int, countryId: String) : this(
        title = title,
        birth = birth,
        gender = gender,
        genderCode = gender.code,
        purposeMask = purposeMask,
        countryId = countryId,
        createdAt = LocalDateTime.now(),
    )

    constructor(id: Long, title: String, birth: LocalDate, gender: Gender, countryId: String, purposes: Set<Purpose>, createdAt: LocalDateTime, lastIndexedAt: LocalDateTime?) : this(
        id = id,
        title = title,
        birth = birth,
        genderCode = gender.code,
        gender = gender,
        countryId = countryId,
        purposeMask = purposes.calculateMask(),
        createdAt = createdAt,
        lastIndexedAt = lastIndexedAt,
    )

    @PrePersist
    fun prePersist() {
        genderCode = gender.code
    }

    @PostLoad
    fun postLoad() {
        gender = Gender.byCode(genderCode)
    }
}
