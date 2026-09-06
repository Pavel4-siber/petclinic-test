package data;

import com.github.javafaker.Faker;
import dto.OwnerDtoRequest;
import dto.PetDto;
import dto.VisitDto;

import java.time.LocalDate;
import java.util.UUID;

public final class TestDataFactory {
    private static final Faker FAKER = new Faker();


    private TestDataFactory() {
    }

    public static OwnerDtoRequest createOwner() {
        String unique = UUID.randomUUID()
                .toString()
                .substring(0, 3);

        return new OwnerDtoRequest(
                FAKER.name().firstName() + unique,
                FAKER.name().lastName() + unique,
                FAKER.address().streetAddress(),
                FAKER.address().city(),
                9 + FAKER.number().digits(9)
        );
    }

    public static PetDto createPet() {
        String unique = UUID.randomUUID()
                .toString()
                .substring(0, 5);

        return new PetDto(
                FAKER.cat().name() + "_" + unique,
                randomBirthDate(),
                FAKER.options().option(
                        "bird", "cat", "dog", "hamster", "lizard", "snake"
                ),
                null
        );
    }

    public static VisitDto createVisit() {
        String unique = UUID.randomUUID()
                .toString()
                .substring(0, 5);

        return new VisitDto(
                LocalDate.now().minusDays(FAKER.number().numberBetween(1, 30)),
                "Test visit_" + unique
        );
    }

    public static VisitDto createInvalidVisit() {
        return new VisitDto(
                LocalDate.now(),
                ""
        );
    }

    private static LocalDate randomBirthDate() {
        return LocalDate.now()
                .minusYears(1 + FAKER.number().numberBetween(0, 15))
                .minusDays(FAKER.number().numberBetween(0, 365));
    }
}
