package dto;

import java.time.LocalDate;
import java.util.List;

public record PetDto(String name,
                     LocalDate birthDate,
                     String type,
                     List<VisitDto> visits) {
}
