package api.model;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@Introspected
public class AddressInfo {
    @NotEmpty
    private final String number;

    @NotEmpty
    private final String street;

    @NotEmpty
    private final String city;

    @NotEmpty
    private final String country;

    @NotEmpty
    private final String postcode;

    public AddressInfo(String number, String street, String city, String country, String postcode) {
        this.number = number;
        this.street = street;
        this.city = city;
        this.country = country;
        this.postcode = postcode;
    }

    public String getNumber() {
        return number;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPostcode() {
        return postcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressInfo that = (AddressInfo) o;
        return number.equals(that.number) && street.equals(that.street) && city.equals(that.city) && country.equals(that.country) && postcode.equals(that.postcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, street, city, country, postcode);
    }
}
