package com.epam.resource_processor.service;

import com.epam.resource_processor.model.MetadataDto;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;

@Service
@AllArgsConstructor
public class ResourceProcessorService {

  private RestTemplate restTemplate;

  @Retryable
  public void processMp3Data(String resourceId)  throws IOException{

    String resourceServiceUrl = "http://localhost:8080/api/v1/resources/mp3/";
    byte[] mp3Data = restTemplate.getForEntity(resourceServiceUrl + resourceId, byte[].class).getBody();

    try {
      restTemplate.postForEntity("http://localhost:8081/api/v1/song", getMetadata(mp3Data), Integer.class);
    } catch (TikaException | SAXException e ) {
      throw new RuntimeException(e);
    }

  }

  private MetadataDto getMetadata(byte[] mp3Data) throws IOException, TikaException, SAXException {
    Mp3Parser mp3Parser = new Mp3Parser();
    BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    ParseContext context = new ParseContext();

    mp3Parser.parse(new ByteArrayInputStream(mp3Data), handler, metadata, context);
    return buildMetadata(metadata);
  }

  private MetadataDto buildMetadata(Metadata metadata) {
    return MetadataDto.builder()
        .year(metadata.get("xmpDM:releaseDate"))
        .artist(metadata.get("xmpDM:artist"))
        .name(metadata.get("dc:title"))
        .album(metadata.get("xmpDM:album"))
        .length(getDurationInMinutes(metadata.get("xmpDM:duration")))
        .build();
  }

  private String getDurationInMinutes(String duration) {
    int value = Integer.parseInt(duration.substring(0, duration.indexOf(".")));
    return (value / 60) + ":" + (value % 60);
  }

}
