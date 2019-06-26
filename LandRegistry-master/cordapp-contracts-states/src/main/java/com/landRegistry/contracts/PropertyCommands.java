package com.landRegistry.contracts;

import net.corda.core.contracts.CommandData;

public interface PropertyCommands extends CommandData {
    class Create implements PropertyCommands {}
    class Transfer implements PropertyCommands {}
    class BankApproval implements PropertyCommands {}
}