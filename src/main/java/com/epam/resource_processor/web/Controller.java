package com.epam.resource_processor.web;

import com.epam.resource_processor.service.ResourceProcessorService;
import java.io.IOException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resource/processor")
public class Controller {

  private final ResourceProcessorService resourceProcessorService;

  public Controller(ResourceProcessorService resourceProcessorService) {
    this.resourceProcessorService = resourceProcessorService;
  }

  @PostMapping
  public void processMp3() throws IOException {
    System.out.println("start");
    resourceProcessorService.processMp3Data("1");
    System.out.println("end");
  }

}
