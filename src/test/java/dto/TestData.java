package dto;

import java.util.List;

public record TestData(
        int ownerId,
        int petId,
        String ownerFirstName,
        String ownerLastName,
        String petName
) {
}