package net.ins.prototype.backend.profile.event

import net.ins.prototype.backend.common.event.UnserializableEvent
import net.ins.prototype.backend.conf.KafkaConf
import net.ins.prototype.backend.profile.service.ProfileIndexService
import net.ins.prototype.common.logger
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class ProfileEventListener(
    private val profileIndexService: ProfileIndexService,
) {

    @KafkaListener(
        topics = ["\${app.integrations.topics.profiles.name}"],
        groupId = "\${app.kafka.consumer.group-id}",
        containerFactory = KafkaConf.PROFILE_EVENT_LISTENER_CONTAINER_FACTORY,
    )
    fun consume(event: ConsumerRecord<Long, ProfileEvent>, ack: Acknowledgment) {
        logger.trace("Received event [key={}; payload={}]", event.key(), event.value())
        when(val payload = event.value()) {
            is UnserializableEvent -> logger.debug("Unserializable event at offset: {}", event.offset())
            is ProfileCreatedEvent -> profileIndexService.index(payload)
            is ProfileUpdatedEvent -> profileIndexService.updateIndex(payload)
        }
        ack.acknowledge()
    }
}
