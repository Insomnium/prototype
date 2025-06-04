package net.ins.prototype.backend.image.web.converter

import net.ins.prototype.backend.common.converter.Converter
import net.ins.prototype.backend.image.dao.model.ImageEntity
import net.ins.prototype.backend.image.web.model.Image
import org.springframework.stereotype.Component

@Component
class ImageResponseConverter : Converter<ImageEntity, Image> {

    override fun convert(source: ImageEntity): Image = Image(
        id = requireNotNull(source.id),
        cdnUri = source.cdnUri,
        primary = source.primary,
    )
}
