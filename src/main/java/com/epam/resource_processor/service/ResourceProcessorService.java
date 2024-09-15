package com.epam.resource_processor.service;

import com.epam.resource_processor.model.MetadataDto;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.epam.resource_processor.util.CustomUtility;
import lombok.AllArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.xml.sax.SAXException;

@Service
@AllArgsConstructor
public class ResourceProcessorService {

    private RestTemplate restTemplate;

    private static final String SONG_SERVICE_URL = "http://localhost:8081/api/v1/song";

    @Retryable
    public void processMp3Data(String s3LocationId) throws IOException {

        Integer s3Id = CustomUtility.toInteger(s3LocationId);
        String resourceServiceUrl = "http://localhost:8080/api/v1/resources/mp3/";

        byte[] mp3Data = restTemplate.getForEntity(resourceServiceUrl + s3LocationId, byte[].class).getBody();

        try {
            restTemplate.postForEntity(SONG_SERVICE_URL, getMetadata(mp3Data, s3Id), Integer.class);
        } catch (TikaException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMp3Metadata(String ids){
        // Build the URL with the query parameter
        String songDeleteUrl = UriComponentsBuilder.fromHttpUrl(SONG_SERVICE_URL + "/delete")
                .queryParam("ids", ids)
                .toUriString();

        // Send the DELETE request
        restTemplate.delete(songDeleteUrl);
    }

    public MetadataDto getMetadata(byte[] mp3Data, Integer s3Id) throws IOException, TikaException, SAXException {
        Mp3Parser mp3Parser = new Mp3Parser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        mp3Parser.parse(new ByteArrayInputStream(mp3Data), handler, metadata, context);
        return buildMetadata(metadata, s3Id);
    }

    private MetadataDto buildMetadata(Metadata metadata, Integer s3Id) {
        return MetadataDto.builder()
                .s3LocationId(s3Id)
                .year(metadata.get("xmpDM:releaseDate"))
                .artist(metadata.get("xmpDM:artist"))
                .name(metadata.get("dc:title"))
                .album(metadata.get("xmpDM:album"))
                .length(getDurationInMinutes(metadata.get("xmpDM:duration")))
                .build();
    }

    public String getDurationInMinutes(String duration) {
        if (duration == null || duration.isEmpty()) {
            return "0:00";  // Default value for duration when it is not available
        }
        int value = Integer.parseInt(duration.substring(0, duration.indexOf(".")));
        return (value / 60) + ":" + (value % 60);
    }

}
