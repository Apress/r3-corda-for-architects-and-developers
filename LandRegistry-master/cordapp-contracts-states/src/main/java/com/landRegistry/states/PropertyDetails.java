package com.landRegistry.states;

import com.google.common.collect.ImmutableList;
import com.landRegistry.schema.PropertyDetailsSchemaV1;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class PropertyDetails implements LinearState, QueryableState {
    private final int propertyId;
    private final String propertyAddress;
    private final int propertyPrice;
    private final int buyerId;
    private final int sellerId;
    private final boolean isMortgageApproved;
    private final boolean isSurveyorApproved;
    private final Party owner;
    private final String description;
    private final String updatedBy;
    private final String updatedTime;

    private final UniqueIdentifier linearId;

    public PropertyDetails(int propertyId,
                           String propertyAddress,
                           int propertyPrice,
                           int buyerId,
                           int sellerId,
                           boolean isMortgageApproved,
                           boolean isSurveyorApproved,
                           Party owner,
                           String description,
                           String updatedBy,
                           String updatedTime) {
        this.propertyId = propertyId;
        this.owner = owner;
        this.propertyAddress = propertyAddress;
        this.propertyPrice = propertyPrice;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.isMortgageApproved = isMortgageApproved;
        this.isSurveyorApproved = isSurveyorApproved;
        this.description = description;
        this.updatedBy = updatedBy;
        this.updatedTime = updatedTime;

        this.linearId = new UniqueIdentifier();
    }

    @ConstructorForDeserialization
    public PropertyDetails(int propertyId,
                           String propertyAddress,
                           int propertyPrice,
                           int buyerId,
                           int sellerId,
                           boolean isMortgageApproved,
                           boolean isSurveyorApproved,
                           Party owner,
                           String description,
                           String updatedBy,
                           String updatedTime,
                           UniqueIdentifier linearId) {
        this.propertyId = propertyId;
        this.owner = owner;
        this.propertyAddress = propertyAddress;
        this.propertyPrice = propertyPrice;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.isMortgageApproved = isMortgageApproved;
        this.isSurveyorApproved = isSurveyorApproved;
        this.description = description;
        this.updatedBy = updatedBy;
        this.updatedTime = updatedTime;

        this.linearId = linearId;
    }

    @NotNull
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(owner);
    }


    public PropertyDetails transfer(Party newOwner) {
        return new PropertyDetails(propertyId,
                propertyAddress,
                propertyPrice,
                buyerId,
                sellerId,
                isMortgageApproved,
                isSurveyorApproved,
                newOwner,
                description,
                updatedBy,
                updatedTime,
                linearId);
    }

    public PropertyDetails approvedByBank(boolean isApproved) {
        return new PropertyDetails(propertyId,
                propertyAddress,
                propertyPrice,
                buyerId,
                sellerId,
                isApproved,
                isSurveyorApproved,
                owner,
                description,
                updatedBy,
                updatedTime,
                linearId);
    }

    public PropertyDetails approvedBySurveyor(boolean isApproved) {
        return new PropertyDetails(propertyId,
                propertyAddress,
                propertyPrice,
                buyerId,
                sellerId,
                isMortgageApproved,
                isApproved,
                owner,
                description,
                updatedBy,
                updatedTime,
                linearId);
    }

    public Party getOwner() {
        return owner;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public int getPropertyPrice() {
        return propertyPrice;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public boolean isMortgageApproved() {
        return isMortgageApproved;
    }

    public boolean isSurveyorApproved() {
        return isSurveyorApproved;
    }

    public String getDescription() {
        return description;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }


    @NotNull
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyDetails that = (PropertyDetails) o;
        return propertyId == that.propertyId &&
                propertyAddress == that.propertyAddress &&
                propertyPrice == that.propertyPrice &&
                owner.equals(that.owner) &&
                buyerId == that.buyerId &&
                sellerId == that.sellerId &&
                isMortgageApproved == that.isMortgageApproved &&
                isSurveyorApproved == that.isSurveyorApproved &&
                description.equals(that.description) &&
                updatedBy.equals(that.updatedBy) &&
                updatedTime.equals(that.updatedTime) &&
                linearId.equals(that.linearId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyId,
                propertyAddress,
                buyerId,
                sellerId,
                isMortgageApproved,
                isSurveyorApproved,
                owner,
                description,
                updatedBy,
                updatedTime,
                linearId);
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new PropertyDetailsSchemaV1());
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof PropertyDetailsSchemaV1) {
            return new PropertyDetailsSchemaV1.PersistentPropertyDetails(
                    this.propertyId,
                    this.propertyAddress,
                    this.propertyPrice,
                    this.buyerId,
                    this.linearId.getId());
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }
}