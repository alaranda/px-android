package com.mercadolibre.dto.preference;

import java.math.BigDecimal;
import java.util.List;
import java.util.OptionalInt;

public class PreferenceShipment {

    private String mode;
    private boolean localPickup;
    private String dimensions;
    private OptionalInt defaultShippingMethod = OptionalInt.empty();
    private List<FreeMethod> freeMethods;
    private BigDecimal cost;
    private boolean freeShipping;
    private ReceiverAddress receiverAddress;

    PreferenceShipment() {
    }

    public String getMode() {
        return mode;
    }

    public boolean getLocalPickup() {
        return localPickup;
    }

    public String getDimensions() {
        return dimensions;
    }

    public OptionalInt getDefaultShippingMethod() {
        return defaultShippingMethod;
    }

    public List<FreeMethod> getFreeMethods() {
        return freeMethods;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public boolean getFreeShipping() {
        return freeShipping;
    }

    public ReceiverAddress getReceiverAddress() {
        return receiverAddress;
    }

    public class FreeMethod {
        private int id;

        public int getId() {
            return id;
        }
    }

    public class ReceiverAddress {

        private String zipCode;
        private String streetName;
        private OptionalInt streetNumber = OptionalInt.empty();
        private String floor;
        private String apartment;

        public ReceiverAddress() {
        }

        public String getStreetName() {
            return streetName;
        }

        public OptionalInt getStreetNumber() {
            return streetNumber;
        }

        public String getZipCode() {
            return zipCode;
        }

        public String getFloor() {
            return floor;
        }

        public String getApartment() {
            return apartment;
        }
    }
}
