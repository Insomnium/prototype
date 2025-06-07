package net.ins.prototype.backend.geo.service

import net.ins.prototype.backend.geo.dao.model.CountryEntity

interface LocationService {

    fun getById(id: String): CountryEntity
}
