package unit.objectmothers;

import com.epam.resource_processor.model.MetadataDto;

public class ObjectMother {

    public static MetadataDto metadataDto(){

        return new MetadataDto(
                "Test song",
                "Test artist",
                "Test album",
                "3:00",
                1,
                "2000"
        );
    }
}
