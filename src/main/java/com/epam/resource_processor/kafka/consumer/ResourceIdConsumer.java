package com.epam.resource_processor.kafka.consumer;

import com.epam.resource_processor.service.ResourceProcessorService;

import java.io.IOException;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResourceIdConsumer {

    private final ResourceProcessorService songProcessorService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceIdConsumer.class);

    @KafkaListener(topics = "resource_id")
    public void consumeSave(String resourceId) throws IOException {
        LOGGER.info("Resource Id received -> {}", resourceId);
        songProcessorService.processMp3Data(resourceId);
    }

    @KafkaListener(topics = "resource_id_del")
    public void consumeDelete(String ids) {
        LOGGER.info("Resource Id deletion -> {}", ids);
        songProcessorService.deleteMp3Metadata(ids);
    }

}
