package net.ins.prototype.backend.geo.dao.repo

import net.ins.prototype.backend.geo.dao.model.CountryEntity
import org.springframework.data.repository.CrudRepository

interface LocationRepository : CrudRepository<CountryEntity, String>
