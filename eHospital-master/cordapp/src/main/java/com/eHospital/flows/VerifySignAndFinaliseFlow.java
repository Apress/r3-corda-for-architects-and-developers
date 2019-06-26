package com.eHospital.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

public class VerifySignAndFinaliseFlow extends FlowLogic<SignedTransaction> {
    private final TransactionBuilder transactionBuilder;

    public VerifySignAndFinaliseFlow(TransactionBuilder transactionBuilder) {
        this.transactionBuilder = transactionBuilder;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {
        transactionBuilder.verify(getServiceHub());
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);
        return subFlow(new FinalityFlow(signedTransaction));
    }
}
