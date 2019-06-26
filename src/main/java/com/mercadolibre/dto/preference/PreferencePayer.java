package com.mercadolibre.dto.preference;


import com.mercadolibre.dto.Address;
import com.mercadolibre.dto.Identification;
import com.mercadolibre.dto.Phone;

public class PreferencePayer {

    private String name;
    private String surname;
    private String email;
    public Phone phone;
    public Identification identification;
    public Address address;

    PreferencePayer() {

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSurname() {
        return surname;
    }

    public Identification getIdentification() {
        return identification;
    }
}
