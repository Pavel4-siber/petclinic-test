package dto;

import java.time.LocalDate;

public record VisitDto(LocalDate visitDate,
                       String description) {
}
