package net.ins.prototype.backend.geo.service.impl

import net.ins.prototype.backend.common.exception.EntityNotFoundException
import net.ins.prototype.backend.geo.dao.model.CountryEntity
import net.ins.prototype.backend.geo.dao.repo.LocationRepository
import net.ins.prototype.backend.geo.service.LocationService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class LocationServiceImpl(
    private val locationRepository: LocationRepository,
) : LocationService {

    override fun getById(id: String): CountryEntity {
        return locationRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("No country found by id: $id")
    }
}
