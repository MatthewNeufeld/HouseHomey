package com.example.househomey;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents an inventory item with a variety of properties
 * @author Lukas Bonkowski, Matthew Neufeld, Owen Cooke
 * @see ItemAdapter
 */
public class Item implements Serializable {
    private String id;
    private String description;
    private Date acquisitionDate;
    private String make = "";
    private String model = "";
    private String serialNumber = "";
    private String comment = "";
    private BigDecimal cost;

    /**
     * This constructs a new item from a Map of data with a reference to its Firestore document
     * @param id The id of this object's document in the firestore database
     * @param data The data from that document to initialize the instance
     * @throws NullPointerException if a null required field is given
     */
    public Item(String id, @NonNull Map<String, Object> data) {
        // Required fields, will throw exceptions
        this.id = Objects.requireNonNull(id);
        this.description = (String) Objects.requireNonNull(data.get("description"));
        this.acquisitionDate = ((Timestamp) Objects.requireNonNull(data.get("acquisitionDate"))).toDate();
        this.cost = new BigDecimal((String) Objects.requireNonNull(data.get("cost"))).setScale(2, RoundingMode.HALF_UP);

        // Optional fields
        if (data.containsKey("make")) {
            this.make = (String) data.get("make");
        }
        if (data.containsKey("model")) {
            this.model = (String) data.get("model");
        }
        if (data.containsKey("serialNumber")) {
            this.serialNumber = (String) data.get("serialNumber");
        }
        if (data.containsKey("comment")) {
            this.comment = (String) data.get("comment");
        }
    }

    /**
     * Convenient getter for map of properties to be used for Firestore queries
     * @return A Map containing all the properties of this object as key-value pairs
     */
    public Map<String, Object> getData() {
        Map<String, Object> itemData = new HashMap<>();

        // Add required properties
        itemData.put("description", description);
        itemData.put("acquisitionDate", new Timestamp(acquisitionDate));
        itemData.put("cost", cost.toString());

        // Only add optional properties if they have non-empty values
        if (!make.isEmpty()) {
            itemData.put("make", make);
        }
        if (!model.isEmpty()) {
            itemData.put("model", model);
        }
        if (!serialNumber.isEmpty()) {
            itemData.put("serialNumber", serialNumber);
        }
        if (!comment.isEmpty()) {
            itemData.put("comment", comment);
        }
        return itemData;
    }


    /**
     * Getter for id
     * @return The id of this item in firestore
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for acquisitionDate
     * @return The acquisition date of this item
     */
    public Date getAcquisitionDate() {
        return acquisitionDate;
    }

    /**
     * Getter for description
     * @return The brief description of this item
     */
    public String getDescription() {
        return description;
    }


    /**
     * Getter for cost
     * @return The cost of this item
     */
    public BigDecimal getCost() {
        return cost;
    }

    /**
     * Getter for make
     * @return make of the item
     */
    public String getMake() { return make; }

    /**
     * Getter for model
     * @return model of the item
     */
    public String getModel() { return model; }

    /**
     * Getter for serial number
     * @return serial number of the item
     */
    public String getSerialNumber() { return serialNumber; }

    /**
     * Getter for comment
     * @return comment for the item
     */
    public String getComment() { return comment; }
}
