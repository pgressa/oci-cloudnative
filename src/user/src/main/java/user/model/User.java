package user.model;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Version;
import user.PasswordUtils;

import javax.validation.constraints.Email;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@MappedEntity("USERS")
public class User {

    @Id
    @AutoPopulated(updateable = false)
    private UUID id;

    @Version
    private Long version;

    private String username;

    private String password;

    private String salt;

    private String firstName;

    private String lastName;

    @Nullable
    @Email
    private String email;

    @Nullable
    private String phone;

    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "user")
    private List<UserAddress> addresses;

    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "user")
    private List<UserCard> cards;

    @DateCreated
    private OffsetDateTime createdAt;

    @DateUpdated
    private OffsetDateTime updatedAt;

    public User() {
    }

    public User(String username, String passwordToHash, String firstName, String lastName, String email, String phone, List<UserAddress> addresses) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.addresses = addresses;
        this.salt = PasswordUtils.generateSalt();
        this.password = PasswordUtils.hash(passwordToHash, salt);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }

    public List<UserAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<UserAddress> addresses) {
        this.addresses = addresses;
    }

    public List<UserCard> getCards() {
        return cards;
    }

    public void setCards(List<UserCard> cards) {
        this.cards = cards;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}