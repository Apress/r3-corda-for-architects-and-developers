package com.eHospital.contracts;

import com.eHospital.states.PatientDataState;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class EHospitalContract implements Contract {
    public static final String ID = "com.eHospital.contracts.EHospitalContract";

    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        verifyAll(tx);
    }

    private void verifyAll(LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<EHospitalCommands> command = requireSingleCommand(tx.getCommands(), com.eHospital.contracts.EHospitalCommands.class);
        com.eHospital.contracts.EHospitalCommands commandType = command.getValue();

        if (commandType instanceof EHospitalCommands.Create) verifyCreateData(tx, command);
    }

    private void verifyCreateData(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("A PatientDataState transaction should consume no input states.", tx.getInputs().isEmpty());
            require.using("A PatientDataState transaction should only create one output state.", tx.getOutputs().size() == 1);

            final PatientDataState out = tx.outputsOfType(PatientDataState.class).get(0);
            require.using("There must only be one signer (hospital) in a PatientDataState transaction.", command.getSigners().size() == 1);
            final Party hospital = out.getOwner();
            require.using("The bidder must be a signer in a PatientDataState transaction.",
                    command.getSigners().containsAll(ImmutableList.of(hospital.getOwningKey())));

            return null;

        });
    }

}