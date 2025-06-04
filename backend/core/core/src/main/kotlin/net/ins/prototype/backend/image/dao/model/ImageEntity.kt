package net.ins.prototype.backend.image.dao.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "images")
class ImageEntity(
    @Id
    @Column(name = "id_image")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "id_profile")
    val profileId: Long,
    @Column(name = "approved")
    val approved: Boolean = false,
    @Column(name = "hidden")
    val hidden: Boolean = false,
    @Column(name = "main")
    var primary: Boolean,
    @Column(name = "folder_uri")
    val folderUri: String,
    @Column(name = "cdn_uri")
    val cdnUri: String,
    @Column(name = "internal_file_name")
    val internalFileName: String,
)
