package com.landRegistry.schema;

import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

/**
 * An PropertyDetails Schema.
 */
public class PropertyDetailsSchemaV1 extends MappedSchema {
    public PropertyDetailsSchemaV1() {
        super(PropertyDetailsSchemaV1.class, 1, ImmutableList.of(PersistentPropertyDetails.class));
    }

    @Entity
    @Table(name = "land_registry_states")
    public static class PersistentPropertyDetails extends PersistentState {
        @Column(name = "propertyId")
        private final int propertyId;
        @Column(name = "propertyAddress")
        private final String propertyAddress;
        @Column(name = "propertyPrice")
        private final int propertyPrice;
        @Column(name = "buyerId")
        private final int buyerId;
        @Column(name = "linear_id")
        private final UUID linearId;


        public PersistentPropertyDetails(int propertyId,
                                         String propertyAddress,
                                         int propertyPrice,
                                         int buyerId,
                                         UUID linearId) {
            this.propertyId = propertyId;
            this.propertyAddress = propertyAddress;
            this.propertyPrice = propertyPrice;
            this.buyerId = buyerId;
            this.linearId = linearId;
        }

        // Default constructor required by hibernate.
        public PersistentPropertyDetails() {
            this.propertyId = 0;
            this.propertyAddress = null;
            this.propertyPrice = 0;
            this.buyerId = 0;
            this.linearId = null;
        }
    }
}