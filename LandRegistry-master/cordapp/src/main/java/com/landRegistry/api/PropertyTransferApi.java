package com.landRegistry.api;

import com.landRegistry.bean.PropertyDetailsBean;
import com.landRegistry.flows.ApprovePropertyFlow;
import com.landRegistry.flows.InitiatePropertyFlow;
import com.landRegistry.flows.TransferPropertyFlow;
import com.landRegistry.schema.PropertyDetailsSchemaV1;
import com.landRegistry.states.PropertyDetails;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.*;

@Path("property")
public class PropertyTransferApi {

    static private final Logger logger = LoggerFactory.getLogger(PropertyTransferApi.class);

    private final CordaRPCOps rpcOps;
    private final CordaX500Name myLegalName;

    public PropertyTransferApi(CordaRPCOps rpcOps) {
        this.rpcOps = rpcOps;
        this.myLegalName = rpcOps.nodeInfo().getLegalIdentities().get(0).getName();
    }

    @GET
    @Path("getAllCurrentPropertyDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<PropertyDetails>> getAllCurrentPropertyDetails() {
        return rpcOps.vaultQuery(PropertyDetails.class).getStates();
    }

    @GET
    @Path("getAllPropertyDetailsForId")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<PropertyDetails>> getAllPropertyDetailsForId(@QueryParam("id") String idString) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(idString);
        List<UniqueIdentifier> linearIds = new ArrayList<>();
        linearIds.add(linearId);

        QueryCriteria linearCriteriaAll = new QueryCriteria.LinearStateQueryCriteria(null,
                linearIds,
                Vault.StateStatus.ALL,
                null);

        return rpcOps.vaultQueryByCriteria(linearCriteriaAll, PropertyDetails.class).getStates();
    }

    /**
     * QueryableState query for retrieving property states of a particular price range
     */
    @GET
    @Path("getBidOffersOfPriceRange")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBidOffersOfPriceRange(@QueryParam("priceRange") int priceRange) throws NoSuchFieldException {
        QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        Field propertyPrice = PropertyDetailsSchemaV1.PersistentPropertyDetails.class.getDeclaredField("propertyPrice");
        CriteriaExpression statusCriteriaExpression = Builder.lessThanOrEqual(propertyPrice, priceRange);
        QueryCriteria statusCriteria = new QueryCriteria.VaultCustomQueryCriteria(statusCriteriaExpression);
        QueryCriteria criteria = generalCriteria.and(statusCriteria);
        List<StateAndRef<PropertyDetails>> results = rpcOps.vaultQueryByCriteria(criteria, PropertyDetails.class).getStates();
        return Response.status(OK).entity(results).build();
    }

    /**
     * QueryableState Experiments ends
     */


    @POST
    @Path("initiate-property-transaction")
    public Response initiatePropertyTransaction(PropertyDetailsBean propertyDetailsBean) {
        Party owner = rpcOps.partiesFromName(propertyDetailsBean.getOwnerString(), false).iterator().next();
        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(InitiatePropertyFlow.class,
                    propertyDetailsBean.getPropertyId(),
                    propertyDetailsBean.getPropertyAddress(),
                    propertyDetailsBean.getPropertyPrice(),
                    propertyDetailsBean.getBuyerId(),
                    propertyDetailsBean.getSellerId(),
                    propertyDetailsBean.getUpdatedBy(),
                    propertyDetailsBean.getUpdatedDateTime(),
                    owner).getReturnValue().get();
            final String msg = String.format("Transaction id %s committed to ledger.\n", signedTx.getId());
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }


    @GET
    @Path("transfer-department-to-surveyer")
    //We can do so for transfer-surveyer-to-department, transfer-department-to-bank, transfer-bank-to-department endpoints
    public Response transferProperty(@QueryParam("id") String idString, @QueryParam("newOwner") String newOwnerString) {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(idString);
        Party newOwner = rpcOps.partiesFromName(newOwnerString, false).iterator().next();
        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(TransferPropertyFlow.class, id, newOwner).getReturnValue().get();
            final String msg = String.format("Transaction id %s committed to ledger.\n", signedTx.getId());
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    @GET
    @Path("approve-bank")
    public Response approveByBank(@QueryParam("id") String idString, @QueryParam("isApproved") boolean isApproved) {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(idString);
        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(ApprovePropertyFlow.class, id, isApproved)
                    .getReturnValue().get();
            final String msg = String.format("approve-bank Transaction id %s committed to ledger.\n", signedTx.getId());
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    @GET
    @Path("approve-surveyor")
    public Response approveBySurveyor(@QueryParam("id") String idString, @QueryParam("isApproved") boolean isApproved) {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(idString);
        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(ApprovePropertyFlow.class, id, isApproved)
                    .getReturnValue().get();
            final String msg = String.format("approve-surveyor Transaction id %s committed to ledger.\n", signedTx.getId());
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }
}
