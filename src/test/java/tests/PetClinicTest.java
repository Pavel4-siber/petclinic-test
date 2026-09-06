package tests;

import core.BaseSeleniumTest;
import data.TestDataFactory;
import dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование petclinic")
public class PetClinicTest extends BaseSeleniumTest {

    @Test
    @Tag("smoke")
    @Tag("owners")
    @Tag("positive")
    @DisplayName("Смоук тест: проверка добавления владельца")
    void shouldAddOwner() throws SQLException {
        OwnerDtoRequest expected = TestDataFactory.createOwner();
        Integer ownerId = null;

        try {
            OwnerDtoResponse actual = petClinicSteps.addOwner(expected);
            ownerId = actual.id();

            assertAll(
                    () -> assertEquals(expected.firstName() + " " + expected.lastName(), actual.name()),
                    () -> assertEquals(expected.address(), actual.address()),
                    () -> assertEquals(expected.city(), actual.city()),
                    () -> assertEquals(expected.telephone(), actual.telephone())
            );
        } finally {
            if (ownerId != null) {
            databaseHelper.deleteOwner(ownerId);
            }
        }
    }

    @Test
    @Tag("smoke")
    @Tag("pets")
    @Tag("positive")
    @DisplayName("Смоук тест: проверка добавления питомца")
    void shouldAddPet() throws SQLException {
        OwnerDtoRequest ownerDto = TestDataFactory.createOwner();

        PetDto expected = TestDataFactory.createPet();

        int ownerId = databaseHelper.saveOwner(ownerDto);

        try {
            List<PetDto> petsDto = petClinicSteps.addPet(ownerDto, expected);

            PetDto actual = findPetByName(petsDto, expected.name());

            assertAll(
                    () -> assertEquals(expected.name(), actual.name()),
                    () -> assertEquals(expected.birthDate(), actual.birthDate()),
                    () -> assertEquals(expected.type(), actual.type())
            );
        } finally {
            databaseHelper.deleteOwner(ownerId);
        }
    }

    @Test
    @Tag("smoke")
    @Tag("visits")
    @Tag("positive")
    @DisplayName("Смоук тест: проверка добавления визита владельца")
    void shouldAddVisit() throws SQLException {
        OwnerDtoRequest ownerDtoRequest = TestDataFactory.createOwner();
        PetDto petDto = TestDataFactory.createPet();
        TestData data = databaseHelper.saveOwnerWithOnePet(ownerDtoRequest, petDto);
        VisitDto expected = TestDataFactory.createVisit();

        try {
            List<PetDto> petsDto = petClinicSteps.addVisit(expected, data);

            PetDto pet = findPetByName(petsDto, data.petName());

            List<VisitDto> visitsDto = pet.visits();

            VisitDto actual = findVisitByDescription(visitsDto, expected.description());

            assertAll(
                    () -> assertEquals(expected.visitDate(), actual.visitDate()),
                    () -> assertEquals(expected.description(), actual.description())
            );
        } finally {
            databaseHelper.deleteTestData(data);
        }
    }

    @Test
    @Tag("regression")
    @Tag("visits")
    @Tag("negative")
    @DisplayName("Негативный тест: валидация визита с пустым описанием")
    void shouldRejectInvalidVisitBlankDescription() throws SQLException {
        String expected = "must not be blank";
        OwnerDtoRequest ownerDtoRequest = TestDataFactory.createOwner();
        PetDto petDto = TestDataFactory.createPet();
        TestData data = databaseHelper.saveOwnerWithOnePet(ownerDtoRequest, petDto);
        VisitDto visitDto = TestDataFactory.createInvalidVisit();

        try {
        String actual = petClinicSteps.addInvalidVisit(data, visitDto);

        assertEquals(expected, actual);
        } finally {
            databaseHelper.deleteTestData(data);
        }
    }

    @Test
    @Tag("owners")
    @Tag("positive")
    @DisplayName("Позитивный тест: нахождение существующего владельца")
    void shouldFindExistingOwner() throws SQLException {
        OwnerDtoRequest ownerDtoRequest = TestDataFactory.createOwner();
        PetDto petDto = TestDataFactory.createPet();
        TestData data = databaseHelper.saveOwnerWithOnePet(ownerDtoRequest, petDto);
        String expected = data.ownerFirstName() + " " + data.ownerLastName();

        try {
        OwnerDtoResponse actual = petClinicSteps.findOwner(data);

        assertEquals(expected, actual.name());
        } finally {
            databaseHelper.deleteTestData(data);
        }
    }

    @Test
    @Tag("owners")
    @Tag("negative")
    @DisplayName("Негативный тест: получение ошибки при поиске несуществующего владельца")
    void shouldShowOwnerNotFoundMessage() {
        String expected = "has not been found";
        String actual = petClinicSteps.getNotFoundOwner("OwnerDefinitelyDoesNotExist");

        assertEquals(expected, actual);
    }

    @Test
    @Tag("system")
    @Tag("negative")
    @DisplayName("Негативный тест: получение страницы ошибки")
    void shouldDisplayErrorPage() {
        String expected = "Something happened...";
        String actual = petClinicSteps.getDisplayError();

        assertEquals(expected, actual);
    }

    @Test
    @Tag("pets")
    @Tag("positive")
    @DisplayName("Позитивный тест: получение списка питомцев для владельца")
    void shouldReturnOnePetForOwner() throws SQLException {
        OwnerDtoRequest ownerDtoRequest = TestDataFactory.createOwner();
        PetDto petDto = TestDataFactory.createPet();
        TestData data = databaseHelper.saveOwnerWithOnePet(ownerDtoRequest, petDto);

        try {
            List<PetDto> actual = petClinicSteps.getPetsByName(data);

            assertAll(
                    () -> assertEquals(1, actual.size()),
                    () -> assertEquals(data.petName(), actual.getFirst().name())
            );
        } finally {
            databaseHelper.deleteTestData(data);
        }
    }

    @Test
    @Tag("regression")
    @Tag("pets")
    @Tag("positive")
    @DisplayName("Позитивный тест: обновление имени питомца")
    void shouldUpdatePetName() throws SQLException {
        OwnerDtoRequest ownerDtoRequest = TestDataFactory.createOwner();
        PetDto petDto = TestDataFactory.createPet();
        TestData data = databaseHelper.saveOwnerWithOnePet(ownerDtoRequest, petDto);
        String expected = "TestPet2";

        try {
            List<PetDto> petsDto = petClinicSteps.updatePetName(data, expected);

            PetDto actual = findPetByName(petsDto, expected);

            assertEquals(expected, actual.name());
        } finally {
            databaseHelper.deleteTestData(data);
        }
    }

    @Test
    @Tag("regression")
    @Tag("pets")
    @Tag("positive")
    @DisplayName("Позитивный тест: обновление даты рождения питомца")
    void shouldUpdatePetBirthDate() throws SQLException {
        OwnerDtoRequest ownerDtoRequest = TestDataFactory.createOwner();
        PetDto petDto = TestDataFactory.createPet();
        TestData data = databaseHelper.saveOwnerWithOnePet(ownerDtoRequest, petDto);
        LocalDate expected = LocalDate.of(2002,2,2);

        try {
            LocalDate actual = petClinicSteps.updatePetBirthDate(data, expected);

            assertEquals(expected, actual);
        } finally {
            databaseHelper.deleteTestData(data);
        }
    }

    @Test
    @Tag("regression")
    @Tag("pets")
    @Tag("negative")
    @DisplayName("Негативный тест: валидация некорректной даты рождения питомца")
    void shouldRejectInvalidPetBirthDate() throws SQLException {
        OwnerDtoRequest ownerDtoRequest = TestDataFactory.createOwner();
        PetDto petDto = TestDataFactory.createPet();
        TestData data = databaseHelper.saveOwnerWithOnePet(ownerDtoRequest, petDto);
        String invalidDate = "02-02-2999";
        String expected = "invalid date";

        try {
            String actual = petClinicSteps.updateInvalidPetBirthDate(data, invalidDate);

            assertEquals(expected, actual);
        } finally {
            databaseHelper.deleteTestData(data);
        }
    }

    @ParameterizedTest(name = "Тип: {0}")
    @ValueSource(strings = {"bird", "cat", "dog", "hamster", "lizard", "snake"})
    @Tag("pets")
    @Tag("regression")
    @Tag("positive")
    @DisplayName("Позитивный тест: обновление типа питомца на допустимые значения")
    void shouldUpdatePetType(String value) throws SQLException {
        OwnerDtoRequest ownerDtoRequest = TestDataFactory.createOwner();
        PetDto petDto = TestDataFactory.createPet();
        TestData data = databaseHelper.saveOwnerWithOnePet(ownerDtoRequest, petDto);

        try {
            List<PetDto> petsDto = petClinicSteps.updatePetType(data, value);
            PetDto actual = findPetByName(petsDto, data.petName());

            assertEquals(value, actual.type());
        } finally {
            databaseHelper.deleteTestData(data);
        }
    }

    @Test
    @Tag("pets")
    @Tag("regression")
    @Tag("positive")
    @DisplayName("Позитивный тест: обновление типа и даты рождения  питомца на допустимые значения")
    void shouldUpdatePetNameBirthDateType() throws SQLException {
        OwnerDtoRequest ownerDtoRequest = TestDataFactory.createOwner();
        PetDto petDto = TestDataFactory.createPet();
        TestData data = databaseHelper.saveOwnerWithOnePet(ownerDtoRequest, petDto);
        PetDto updatePet = TestDataFactory.createPet();

        try {
            List<PetDto> petsDto = petClinicSteps.updatePetNameBirthDateType(data, updatePet);

            PetDto actual = findPetByName(petsDto, updatePet.name());

            assertAll(
                    () -> assertEquals(updatePet.name(), actual.name()),
                    () -> assertEquals(updatePet.birthDate(), actual.birthDate()),
                    () -> assertEquals(updatePet.type(), actual.type())
            );
        } finally {
            databaseHelper.deleteTestData(data);
        }
    }

    @Test
    @Tag("pets")
    @Tag("regression")
    @Tag("positive")
    @DisplayName("Позитивный тест: получение типа питомца")
    void shouldGetPetType() throws SQLException {
        OwnerDtoRequest ownerDtoRequest = TestDataFactory.createOwner();
        PetDto petDto = TestDataFactory.createPet();
        TestData data = databaseHelper.saveOwnerWithOnePet(ownerDtoRequest, petDto);
        String expected = petDto.type();

        try {
            String actual = petClinicSteps.getPetByType(data);

            assertEquals(expected, actual);
        } finally {
            databaseHelper.deleteTestData(data);
        }
    }

    private static PetDto findPetByName(List<PetDto> petsDto, String petName) {
        return petsDto.stream()
                .filter(p -> p.name().equals(petName))
                .findFirst()
                .orElseThrow();
    }

    private static VisitDto findVisitByDescription(List<VisitDto> actual, String description) {
        return actual.stream()
                .filter(v -> v.description().equals(description))
                .findFirst()
                .orElseThrow();
    }
}
