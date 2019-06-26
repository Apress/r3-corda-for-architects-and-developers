package com.eHospital.states;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class PatientDataState implements LinearState {
    private final Party owner;
    private final String userId;
    private final SecureHash attachmentHashValue;

    private final UniqueIdentifier linearId;


    public PatientDataState(Party owner, String userId, SecureHash attachmentHashValue) {
        this.owner = owner;
        this.userId = userId;
        this.attachmentHashValue = attachmentHashValue;
        this.linearId = new UniqueIdentifier();
    }

    @ConstructorForDeserialization
    public PatientDataState(Party owner, String userId, SecureHash attachmentHashValue, UniqueIdentifier linearId) {
        this.owner = owner;
        this.userId = userId;
        this.attachmentHashValue = attachmentHashValue;
        this.linearId = linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(owner);
    }

    public Party getOwner() {
        return owner;
    }

    public String getUserId() {
        return userId;
    }

    public SecureHash getAttachmentHashValue() {
        return attachmentHashValue;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public PatientDataState transfer(Party newOwner) {
        return new PatientDataState(newOwner,
                userId,
                attachmentHashValue,
                linearId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, userId, linearId);
    }
}