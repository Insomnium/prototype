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
import java.time.LocalDate

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
) {

    constructor(title: String, birth: LocalDate, gender: Gender, purposeMask: Int, countryId: String) : this(
        title = title,
        birth = birth,
        gender = gender,
        genderCode = gender.code,
        purposeMask = purposeMask,
        countryId = countryId,
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
