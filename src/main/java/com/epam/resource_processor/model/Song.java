package com.epam.resource_processor.model;

import lombok.Data;

@Data
public class Song {

  private Integer id;
  private String name;
  private String artist;
  private String album;
  private String length;
  private Integer resourceId;
  private String year;

}
