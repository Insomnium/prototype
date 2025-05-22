package net.ins.prototype.backend.profile.dao.model

import net.ins.prototype.backend.profile.model.Gender
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDate

@Document(indexName = "profile")
data class ProfileEsEntity(
    @Id
    val id: String? = null,
    @Field(type = FieldType.Long)
    val dbId: Long,
    @Field(type = FieldType.Text)
    val gender: Gender,
    @Field(type = FieldType.Date)
    val birth: LocalDate,
    @Field(type = FieldType.Text)
    val countryId: String,
    @Field(type = FieldType.Auto)
    val purpose: PurposeEsSubEntity,
)

data class PurposeEsSubEntity(
    @Field(type = FieldType.Boolean)
    val dating: Boolean,
    @Field(type = FieldType.Boolean)
    val sexting: Boolean,
    @Field(type = FieldType.Boolean)
    val relationships: Boolean,
)
