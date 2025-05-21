package net.ins.prototype.backend.profile.event

import com.github.avrokotlin.avro4k.Avro
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.github.classgraph.ClassGraph
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.Deserializer
import kotlin.reflect.KClass

@OptIn(InternalSerializationApi::class)
class Avro4kKotlinProfileDeserializer<T : Any>(
    schemaRegistryClient: SchemaRegistryClient,
) : Deserializer<ProfileEvent> {
    private val avro = Avro.default
    private val eventClasses: Map<String, KClass<out ProfileEvent>> = findEventClasses()
    private val vanillaDeserializer = KafkaAvroDeserializer(schemaRegistryClient)

    override fun deserialize(topic: String, data: ByteArray?): ProfileEvent? {
        return data?.let {
            val genericRecord = vanillaDeserializer.deserialize(topic, it) as GenericRecord
            val eventClass = requireNotNull(eventClasses[genericRecord.schema.name])
            return avro.fromRecord(eventClass.serializer(), genericRecord)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun findEventClasses(): Map<String, KClass<out ProfileEvent>> {
        val debug =  ClassGraph()
            .enableClassInfo()
            .acceptPackages("net.ins.prototype.backend.profile.event")
            .scan()
            .use { clazz ->
                clazz.getClassesImplementing(ProfileEvent::class.java.name).standardClasses.associate { it.simpleName to it.loadClass().kotlin as KClass<out ProfileEvent> }
            }
        return debug
    }
}
