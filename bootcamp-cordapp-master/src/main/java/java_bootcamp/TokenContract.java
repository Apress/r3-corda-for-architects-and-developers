package java_bootcamp;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract{
    public static String ID = "java_bootcamp.TokenContract";

    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        CommandData commandType = tx.getCommand(0).getValue();

        if (commandType instanceof Issue) {

            // Contract expects transaction to have no input, or else exception would be thrown
            if (tx.getInputStates().size() != 0)
                throw new IllegalArgumentException("Transactions must have zero inputs");

            // Contract expects transaction to have only one output, or else exception would be thrown
            if (tx.getOutputStates().size() != 1)
                throw new IllegalArgumentException("Transactions must have one output");

            // Contract expects transaction to have only one command, or else exception would be thrown
            if (tx.getCommands().size() != 1)
                throw new IllegalArgumentException("Transactions must have one command");

            // Amount in transaction should be positive
            TokenState tokenState = (TokenState) tx.getOutput(0);
            if (tokenState.getAmount() <= 0)
                throw new IllegalArgumentException("Transactions must have positive amount");


            Command command = tx.getCommand(0);
            List<PublicKey> requiredSigners = command.getSigners();

            //Required Signer constraints
            ContractState outputState = tx.getOutput(0);

            Party party = (Party) outputState.getParticipants().get(0);
            PublicKey ownersKey = party.getOwningKey();
            if (!requiredSigners.contains(ownersKey)) {
                throw new IllegalArgumentException("Party must sign registration");
            }
        } else {
            throw new IllegalArgumentException("Transactions command must be of type Issue");
        }
    }

    public static class Issue implements CommandData {}
}