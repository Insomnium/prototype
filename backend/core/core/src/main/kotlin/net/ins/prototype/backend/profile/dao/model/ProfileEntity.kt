package net.ins.prototype.backend.profile.dao.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.PostLoad
import jakarta.persistence.PrePersist
import jakarta.persistence.PrimaryKeyJoinColumn
import jakarta.persistence.Table
import net.ins.prototype.backend.profile.model.Gender
import java.time.LocalDate

@Entity
@Table(name = "profiles")
class ProfileEntity(
    @Id
    @Column(name = "id_profile")
    var id: Long? = null,
    @Column(name = "title")
    val title: String,
    @Column(name = "birth")
    val birth: LocalDate,
    @Column(name = "gender")
    var genderCode: Char,
    @Transient
    var gender: Gender,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @PrimaryKeyJoinColumn
    var interest: InterestEntity,
) {

    @PrePersist
    fun prePersist() {
        genderCode = gender.code
    }

    @PostLoad
    fun postLoad() {
        gender = Gender.byCode(genderCode)
    }
}
