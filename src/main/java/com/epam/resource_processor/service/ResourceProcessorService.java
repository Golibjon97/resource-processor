package com.epam.resource_processor.service;

import com.epam.resource_processor.model.MetadataDto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.epam.resource_processor.util.Operations;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.xml.sax.SAXException;

@Service
@Slf4j
public class ResourceProcessorService {

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;


    @Autowired
    public ResourceProcessorService(RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    @Retryable
    public void processMp3Data(String s3LocationId) throws IOException {

        String resourceServiceUrl = getUri(Operations.GET_RESOURCE) + s3LocationId;
        String songServiceUrl = getUri(Operations.POST_SONG);

        log.info("sent data to resource service: {}", resourceServiceUrl);
        byte[] mp3Data = restTemplate.getForEntity(resourceServiceUrl, byte[].class).getBody();
        log.info("received data from resource service: {}", resourceServiceUrl);

        try {
            log.info("sent data to song service");
            Integer songId =
                    restTemplate.postForEntity(songServiceUrl, getMetadata(mp3Data, s3LocationId), Integer.class).getBody();
            log.info("received data from song service with id: {}", songId);
        } catch (TikaException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMp3Metadata(String ids){
        log.info("Processing song deletion with ids: {}", ids);
        String songServiceUrl = getUri(Operations.DELETE_SONG);

        String songDeleteUrl = UriComponentsBuilder.fromHttpUrl(songServiceUrl)
                .queryParam("ids", ids)
                .toUriString();

        restTemplate.delete(songDeleteUrl);
        log.info("Deletion processing completed");
    }

    public MetadataDto getMetadata(byte[] mp3Data, String s3LocationId) throws IOException, TikaException, SAXException {
        Mp3Parser mp3Parser = new Mp3Parser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        mp3Parser.parse(new ByteArrayInputStream(mp3Data), handler, metadata, context);
        return buildMetadata(metadata, s3LocationId);
    }

    private MetadataDto buildMetadata(Metadata metadata, String s3LocationId) {
        return MetadataDto.builder()
                .s3LocationId(Integer.valueOf(s3LocationId))
                .year(metadata.get("xmpDM:releaseDate"))
                .artist(metadata.get("xmpDM:artist"))
                .name(metadata.get("dc:title"))
                .album(metadata.get("xmpDM:album"))
                .length(getDurationInMinutes(metadata.get("xmpDM:duration")))
                .build();
    }

    public String getDurationInMinutes(String duration) {
        if (duration == null || duration.isEmpty()) {
            return "0:00";
        }
        int value = Integer.parseInt(duration.substring(0, duration.indexOf(".")));
        return (value / 60) + ":" + (value % 60);
    }

    private String getUri(Operations operationName){
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("spring-cloud-gateway");
        ServiceInstance serviceInstance = serviceInstances.get(0);
        String uri = serviceInstance.getUri().toString();

        return switch (operationName) {
            case GET_RESOURCE -> uri + "/api/v1/resources/mp3/";
            case POST_SONG -> uri + "/api/v1/song";
            case DELETE_SONG -> uri + "/api/v1/song/delete";
        };

    }

}
