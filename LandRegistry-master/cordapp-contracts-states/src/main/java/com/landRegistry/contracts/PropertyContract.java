package com.landRegistry.contracts;

import com.google.common.collect.ImmutableList;
import com.landRegistry.states.PropertyDetails;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class PropertyContract implements Contract {
    public static final String ID = "com.landRegistry.contracts.PropertyContract";

    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        verifyAll(tx);
    }

    private void verifyAll(LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<PropertyCommands> command = requireSingleCommand(tx.getCommands(), PropertyCommands.class);
        PropertyCommands commandType = command.getValue();

        if (commandType instanceof PropertyCommands.Create) verifyCreate(tx, command);
        else if (commandType instanceof PropertyCommands.Transfer) verifyTransfer(tx, command);
        else if (commandType instanceof PropertyCommands.BankApproval) verifyBankApproval(tx, command);
    }

    private void verifyCreate(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("A Property Transfer transaction should consume no input states.",
                    tx.getInputs().isEmpty());
            require.using("A Property Transfer transaction should only create one output state.",
                    tx.getOutputs().size() == 1);

            final PropertyDetails out = tx.outputsOfType(PropertyDetails.class).get(0);
            return null;
        });
    }

    private void verifyTransfer(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("A Property Transfer transaction should only consume one input state.",
                    tx.getInputs().size() == 1);
            require.using("A Property Transfer transaction should only create one output state.",
                    tx.getOutputs().size() == 1);

            final PropertyDetails in = tx.inputsOfType(PropertyDetails.class).get(0);
            final PropertyDetails out = tx.outputsOfType(PropertyDetails.class).get(0);

            require.using("The owner Property must change in a Property Transfer transaction.",
                    in.getOwner() != out.getOwner());

            require.using("There must only be one signer (the current owner) in a Property Transfer transaction.",
                    command.getSigners().size() == 1);

            return null;
        });
    }

    private void verifyBankApproval(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            //Add some more checks on your own
            return null;
        });
    }
}
