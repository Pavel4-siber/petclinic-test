package database;

import dto.*;

import java.sql.*;
import java.time.LocalDate;

public class DatabaseHelper {

    private static final String URL = "jdbc:postgresql://localhost:5433/petclinic";

    private static final String USER = "petclinic";
    private static final String PASSWORD = "petclinic";


    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private int saveOwnerOnDB(OwnerDtoRequest owner) throws SQLException {
        String firstName = owner.firstName();
        String lastName = owner.lastName();
        String address = owner.address();
        String city = owner.city();
        String telephone = owner.telephone();

        String sql = """
            INSERT INTO owners (
                first_name,
                last_name,
                address,
                city,
                telephone
            )
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, address);
            statement.setString(4, city);
            statement.setString(5, telephone);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            return resultSet.getInt("id");
        }
    }

    private int savePetOnDB(PetDto pet, int ownerId) throws SQLException {
        String name = pet.name();
        LocalDate birthDate = pet.birthDate();
        String type = pet.type();

        String sql = """
            INSERT INTO pets (
                name,
                birth_date,
                type_id,
                owner_id
            )
            SELECT ?, ?, id, ?
            FROM types
            WHERE name = ?
            RETURNING id
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setDate(2, Date.valueOf(birthDate));
            statement.setInt(3, ownerId);
            statement.setString(4, type);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            return resultSet.getInt("id");
        }
    }

    private int saveVisitOnDB(VisitDto visit, int petId) throws SQLException {
        LocalDate visitDate = visit.visitDate();
        String description = visit.description();

        String sql = """
            INSERT INTO visits (
                pet_id,
                visit_date,
                description
            )
            VALUES (?, ?, ?)
            RETURNING id
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, petId);
            statement.setDate(2, Date.valueOf(visitDate));
            statement.setString(3, description);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            return resultSet.getInt("id");
        }
    }

    public int saveOwner(OwnerDtoRequest owner) throws SQLException {
        return saveOwnerOnDB(owner);
    }

    public int savePet(PetDto pet, int ownerId) throws SQLException {
        return savePetOnDB(pet, ownerId);
    }

    public int saveVisit(VisitDto visit, int petId) throws SQLException {
        return saveVisitOnDB(visit, petId);
    }

    public void deleteVisit(Integer visitId) throws SQLException {
        if (visitId == null) {
            return;
        }
        String sql = "DELETE FROM visits WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, visitId);
            statement.executeUpdate();
        }
    }

    public void deletePet(Integer petId) throws SQLException {
        if (petId == null) {
            return;
        }
        String sql = "DELETE FROM pets WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, petId);
            statement.executeUpdate();
        }
    }

    public void deleteOwner(Integer ownerId) throws SQLException {

        if (ownerId == null) {
            return;
        }

        String deleteVisits = """
            DELETE FROM visits
            WHERE pet_id IN (
                SELECT id FROM pets WHERE owner_id = ?
            )
            """;

        String deletePets = """
            DELETE FROM pets
            WHERE owner_id = ?
            """;

        String deleteOwner = """
            DELETE FROM owners
            WHERE id = ?
            """;

        try (Connection connection = getConnection()) {

            connection.setAutoCommit(false);

            try {
                executeDelete(connection, deleteVisits, ownerId);
                executeDelete(connection, deletePets, ownerId);
                executeDelete(connection, deleteOwner, ownerId);

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    private void executeDelete(
            Connection connection,
            String sql,
            int ownerId) throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ownerId);
            statement.executeUpdate();
        }
    }

    public TestData saveOwnerWithPetAndVisit(OwnerDtoRequest owner, PetDto pet, VisitDto visit) throws SQLException {
        int ownerId = saveOwnerOnDB(owner);
        int petId = savePetOnDB(pet, ownerId);
        int visitId = saveVisitOnDB(visit, petId);

        OwnerDtoRequest ownerDtoRequest = findOwnerById(ownerId);
        PetDto petDto = findPetById(petId);

        return new TestData(
                ownerId,
                petId,
                ownerDtoRequest.firstName(),
                ownerDtoRequest.lastName(),
                petDto.name()
        );
    }

    public void deleteTestData(TestData data) throws SQLException {

        String deleteVisit = """
            DELETE FROM visits
            WHERE pet_id = ?
            """;

        String deletePet = """
            DELETE FROM pets
            WHERE id = ?
            """;

        String deleteOwner = """
            DELETE FROM owners
            WHERE id = ?
            """;

        try (Connection connection = getConnection()) {

            try (PreparedStatement statement =
                         connection.prepareStatement(deleteVisit)) {
                statement.setInt(1, data.petId());
                statement.executeUpdate();
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(deletePet)) {
                statement.setInt(1, data.petId());
                statement.executeUpdate();
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(deleteOwner)) {
                statement.setInt(1, data.ownerId());
                statement.executeUpdate();
            }
        }
    }

    public TestData saveOwnerWithOnePet(OwnerDtoRequest owner, PetDto pet) throws SQLException {
        int ownerId = saveOwnerOnDB(owner);
        int petId = savePetOnDB(pet, ownerId);

        return new TestData(
                ownerId,
                petId,
                owner.firstName(),
                owner.lastName(),
                pet.name()
        );
    }

    public OwnerDtoRequest findOwnerById(int ownerId) throws SQLException {
        String sql = """
        SELECT id, first_name, last_name, address, city, telephone
        FROM owners
        WHERE id = ?
        """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, ownerId);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Owner not found: " + ownerId);
                }

                return new OwnerDtoRequest(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("telephone")
                );
            }
        }
    }

    private PetDto findPetById(int petId) throws SQLException {
        String sql = """
        SELECT
            p.name,
            p.birth_date,
            t.name AS type
        FROM pets p
        JOIN types t ON t.id = p.type_id
        WHERE p.id = ?
        """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, petId);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Pet not found: " + petId);
                }

                return new PetDto(
                        rs.getString("name"),
                        rs.getObject("birth_date", LocalDate.class),
                        rs.getString("type"),
                        null
                );
            }
        }
    }
}
