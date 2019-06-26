package com.landRegistry.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.landRegistry.contracts.PropertyCommands;
import com.landRegistry.contracts.PropertyContract;
import com.landRegistry.states.PropertyDetails;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

@InitiatingFlow
@StartableByRPC
public class ApprovePropertyFlow extends FlowLogic<SignedTransaction> {
    private final ProgressTracker progressTracker = new ProgressTracker();
    private final UniqueIdentifier linearId;
    private final boolean isApproved;

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    public ApprovePropertyFlow(UniqueIdentifier linearId, boolean isApproved) {
        this.linearId = linearId;
        this.isApproved = isApproved;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        StateAndRef<PropertyDetails> inputStateAndRef = getServiceHub().getVaultService().queryBy(PropertyDetails.class,
                queryCriteria).getStates().get(0);

        Party owner = inputStateAndRef.getState().getData().getOwner();
        PropertyDetails propertyDetails = null;

        if(owner.getName().toString().contains("Bank")) {
            propertyDetails = inputStateAndRef.getState().getData().approvedByBank(isApproved);
        } else if(owner.getName().toString().contains("Surveyor")) {
            propertyDetails = inputStateAndRef.getState().getData().approvedBySurveyor(isApproved);
        }

        TransactionBuilder builder = new TransactionBuilder(inputStateAndRef.getState().getNotary())
                .addInputState(inputStateAndRef)
                .addOutputState(propertyDetails, PropertyContract.ID)
                .addCommand(new PropertyCommands.BankApproval(), getOurIdentity().getOwningKey());

        return subFlow(new VerifySignAndFinaliseFlow(builder));
    }
}
