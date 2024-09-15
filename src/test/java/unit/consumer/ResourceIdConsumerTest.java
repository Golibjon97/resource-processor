package unit.consumer;

import com.epam.resource_processor.kafka.consumer.ResourceIdConsumer;
import com.epam.resource_processor.service.ResourceProcessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceIdConsumerTest {

    @Mock
    private ResourceProcessorService songProcessorService;

    @InjectMocks
    private ResourceIdConsumer resourceIdConsumer;

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceIdConsumer.class);

    @BeforeEach
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    public void testConsume_withValidResourceId() throws IOException {
//        // Arrange
//        String resourceId = "sampleResourceId";
//
//        doNothing().when(songProcessorService).processMp3Data(resourceId);
//
//        // Act
//        resourceIdConsumer.consume(resourceId);
//
//        // Assert
//        verify(songProcessorService).processMp3Data(resourceId);
//    }
}
