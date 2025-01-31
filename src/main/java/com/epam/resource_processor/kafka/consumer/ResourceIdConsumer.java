package com.epam.resource_processor.kafka.consumer;

import com.epam.resource_processor.service.ResourceProcessorService;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@AllArgsConstructor
@Slf4j
public class ResourceIdConsumer {

    private final ResourceProcessorService songProcessorService;

    @KafkaListener(topics = "resource_id")
    public void consumeSave(ConsumerRecord<String, String> record) throws IOException {
        String resourceId = record.value();
        log.info("Resource Id received -> {}", resourceId);
        setTraceIdFromHeader(record);

        try{
            songProcessorService.processMp3Data(resourceId);
        } finally {
            MDC.remove("trace-id");
        }

    }

    @KafkaListener(topics = "resource_id_del")
    public void consumeDelete(ConsumerRecord<String, String> record) {
        String ids = record.value();
        log.info("Resource Id deletion -> {}", ids);
        setTraceIdFromHeader(record);

        try{
            songProcessorService.deleteMp3Metadata(ids);
        } finally {
            MDC.remove("trace-id");
        }

    }

    private void setTraceIdFromHeader(ConsumerRecord<String, String> record) {
        Header traceIdHeader = record.headers().lastHeader("trace-id");
        if (traceIdHeader != null) {
            String traceId = new String(traceIdHeader.value());
            MDC.put("trace-id", traceId);
        }
    }

}
