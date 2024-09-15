package com.epam.resource_processor.model;

import lombok.*;


@Data
@Builder
public class MetadataDto {
  private String name;
  private String artist;
  private String album;
  private String length;
  private Integer s3LocationId;
  private String year;
}
