package com.eHospital.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.eHospital.contracts.EHospitalCommands;
import com.eHospital.contracts.EHospitalContract;
import com.eHospital.states.PatientDataState;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;

@InitiatingFlow
@StartableByRPC
public class CreatePatientDataFlow extends FlowLogic<SignedTransaction> {
    private final Party ownerHospital;
    private final String userId;
    private final SecureHash attachmentHashValue;

    private final Step GENERATING_TRANSACTION = new Step("Generating transaction CreatePatientDataFlow.");
    private final Step VERIFYING_TRANSACTION = new Step("Verifying contract constraints CreatePatientDataFlow.");
    private final Step SIGNING_TRANSACTION = new Step("Signing transaction with our private key CreatePatientDataFlow.");
    private final Step GATHERING_SIGS = new Step("Gathering the counterparty's signature CreatePatientDataFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return CollectSignaturesFlow.Companion.tracker();
        }
    };
    private final Step FINALISING_TRANSACTION = new Step("Obtaining notary signature and recording transaction CreatePatientDataFlow.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    public CreatePatientDataFlow(Party ownerHospital,
                                 String userId,
                                 SecureHash attachmentHashValue) {
        this.ownerHospital = ownerHospital;
        this.userId = userId;
        this.attachmentHashValue = attachmentHashValue;
    }

    // The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
    // checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call()
    // function.
    private final ProgressTracker progressTracker = new ProgressTracker(
            GENERATING_TRANSACTION,
            SIGNING_TRANSACTION,
            FINALISING_TRANSACTION
    );

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        PatientDataState patientDataState = new PatientDataState(ownerHospital, userId, attachmentHashValue);

        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        TransactionBuilder builder = new TransactionBuilder(notary)
                .addOutputState(patientDataState, EHospitalContract.ID)
                .addAttachment(attachmentHashValue)
                .addCommand(new EHospitalCommands.Create(), getOurIdentity().getOwningKey());

        progressTracker.setCurrentStep(FINALISING_TRANSACTION);
        return subFlow(new com.eHospital.flows.VerifySignAndFinaliseFlow(builder));

    }
}