package unit.service;

import com.epam.resource_processor.model.MetadataDto;
import com.epam.resource_processor.service.ResourceProcessorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ResourceProcessorService resourceProcessorService;

    @Test
    void processMp3DataTest() throws IOException {

        String resourceServiceUrl = "http://localhost:8080/api/v1/resources/mp3/";
        String songServiceUrl = "http://localhost:8081/api/v1/song";
        byte[] mp3Data = "Test song".getBytes();
        String resourceId = "1";

        when(restTemplate.getForEntity(resourceServiceUrl + resourceId, byte[].class))
                .thenReturn(new ResponseEntity<>(mp3Data, HttpStatus.OK));
        when(restTemplate.postForEntity(eq(songServiceUrl), any(MetadataDto.class), eq(Integer.class)))
                .thenReturn(new ResponseEntity<>(1, HttpStatus.OK));

        resourceProcessorService.processMp3Data(resourceId);

        verify(restTemplate).getForEntity(resourceServiceUrl + resourceId, byte[].class);
        verify(restTemplate).postForEntity(eq(songServiceUrl), any(MetadataDto.class), eq(Integer.class));
    }
}
