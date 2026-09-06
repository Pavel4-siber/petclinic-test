package steps;

import dto.*;
import io.qameta.allure.Step;
import page.MainPage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;

public class PetClinicSteps {

    public PetClinicSteps() {
    }

    private static final DateTimeFormatter UI_DATE_FORMAT =
            ofPattern("dd-MM-yyyy");

    @Step("Add owner: {owner.firstName} {owner.lastName}")
    public OwnerDtoResponse addOwner(OwnerDtoRequest owner) {
        return new MainPage().clickFindOwnerPage()
                .addOwner()
                .addOwner(owner.firstName(), owner.lastName(), owner.address(), owner.city(), owner.telephone())
                .getOwner();
    }

    @Step("Add pet '{pet.name}' for owner '{owner.lastName}'")
    public List<PetDto> addPet(OwnerDtoRequest owner, PetDto pet) {
        return new MainPage()
                .clickFindOwnerPage()
                .findOwnerWithTrueParam(owner.lastName())
                .addPet()
                .addPet(pet.name(), pet.birthDate().format(UI_DATE_FORMAT), pet.type())
                .getPets();
    }

    @Step("Add visit '{visit.description}' to pet '{data.petName}'")
    public List<PetDto> addVisit(VisitDto visit, TestData data) {
        return new MainPage()
                .clickFindOwnerPage()
                .findOwnerWithTrueParam(data.ownerLastName())
                .addVisit(data.petName())
                .addNewVisit(visit.visitDate().format(UI_DATE_FORMAT), visit.description())
                .getPets();
    }

    @Step("Try to add visit with blank description")
    public String addInvalidVisit(TestData data, VisitDto visit) {
        return new MainPage().clickFindOwnerPage()
                .findOwnerWithTrueParam(data.ownerLastName())
                .addVisit(data.petName())
                .addNewVisitWithDefaultDescription(visit.visitDate().format(UI_DATE_FORMAT), visit.description());
    }

    @Step("Find owner '{data.ownerLastName}'")
    public OwnerDtoResponse findOwner(TestData data) {
        return new MainPage().clickFindOwnerPage()
                .findOwnerWithTrueParam(data.ownerLastName())
                .getOwner();
    }

    @Step("Try found owner with '{name}'")
    public String getNotFoundOwner(String name) {
        return new MainPage().clickFindOwnerPage()
                .findOwnerWithFalseParam(name);
    }

    @Step("Get display error")
    public String getDisplayError() {
        return new MainPage().clickErrorPage()
                .getTextFromPage();
    }

    @Step("Get pets for owner '{data.ownerLastName}'")
    public List<PetDto> getPetsByName(TestData data) {
        return new MainPage().clickFindOwnerPage()
                .findOwnerWithTrueParam(data.ownerLastName())
                .getPets();
    }

    @Step("Get pets for owner '{data.ownerLastName}'")
    public String getPetByType(TestData data) {
        return new MainPage().clickFindOwnerPage()
                .findOwnerWithTrueParam(data.ownerLastName())
                .editPet(data.petName())
                .getType();
    }

    @Step("Update pet name to '{name}'")
    public List<PetDto> updatePetName(TestData data, String name) {
        return new MainPage().clickFindOwnerPage()
                .findOwnerWithTrueParam(data.ownerLastName())
                .editPet(data.petName())
                .updateNamePet(name)
                .getPets();
    }

    @Step("Update pet birthdate to '{date}'")
    public LocalDate updatePetBirthDate(TestData data, LocalDate date) {
        String birthDate = new MainPage().clickFindOwnerPage()
                .findOwnerWithTrueParam(data.ownerLastName())
                .editPet(data.petName())
                .updateBirthDate(date.format(UI_DATE_FORMAT))
                .getBirthDate();
        return LocalDate.parse(birthDate);
    }

    @Step("Try to update pet birth date to invalid value '{date}'")
    public String updateInvalidPetBirthDate(TestData data, String date) {
        return new MainPage().clickFindOwnerPage()
                .findOwnerWithTrueParam(data.ownerLastName())
                .editPet(data.petName())
                .updateBirthDateWithInvalidDate(date);
    }

    @Step("Update pet type to '{value}'")
    public List<PetDto> updatePetType(TestData data, String value) {
        return new MainPage().clickFindOwnerPage()
                .findOwnerWithTrueParam(data.ownerLastName())
                .editPet(data.petName())
                .updateTypePet(value)
                .getPets();
    }

    public List<PetDto> updatePetNameBirthDateType(TestData data, PetDto pet) {
        return new MainPage()
                .clickFindOwnerPage()
                .findOwnerWithTrueParam(data.ownerLastName())
                .editPet(data.petName())
                .update_name_birth_type_pet(
                        pet.name(),
                        pet.birthDate().format(UI_DATE_FORMAT),
                        pet.type()
                )
                .getPets();
    }
}
