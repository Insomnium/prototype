package net.ins.prototype.backend.geo.dao.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "countries")
class CountryEntity(
    @Id
    @Column(name = "id")
    val id: String,
    @Column(name = "name")
    val name: String,
    @Column(name = "region_id")
    val regionId: String,
)
