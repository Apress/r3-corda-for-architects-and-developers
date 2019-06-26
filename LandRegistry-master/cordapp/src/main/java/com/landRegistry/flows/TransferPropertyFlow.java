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
public class TransferPropertyFlow extends FlowLogic<SignedTransaction> {
    private final ProgressTracker progressTracker = new ProgressTracker();
    private final UniqueIdentifier linearId;
    private final Party newOwner;

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    public TransferPropertyFlow(UniqueIdentifier linearId, Party newOwner) {
        this.linearId = linearId;
        this.newOwner = newOwner;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        StateAndRef<PropertyDetails> inputStateAndRef = getServiceHub().getVaultService().queryBy(PropertyDetails.class,
                queryCriteria).getStates().get(0);

        PropertyDetails propertyDetails = inputStateAndRef.getState().getData().transfer(newOwner);

        TransactionBuilder builder = new TransactionBuilder(inputStateAndRef.getState().getNotary())
                .addInputState(inputStateAndRef)
                .addOutputState(propertyDetails, PropertyContract.ID)
                .addCommand(new PropertyCommands.Transfer(), getOurIdentity().getOwningKey());

        return subFlow(new VerifySignAndFinaliseFlow(builder));
    }
}
