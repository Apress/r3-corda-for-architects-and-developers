package com.eHospital.contracts;

import net.corda.core.contracts.CommandData;

public interface EHospitalCommands extends CommandData {
    class Create implements EHospitalCommands {}
}