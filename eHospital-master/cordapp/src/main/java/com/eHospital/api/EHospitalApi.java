package com.eHospital.api;

import com.eHospital.flows.CreatePatientDataFlow;
import com.eHospital.states.PatientDataState;
import com.google.common.collect.ImmutableMap;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;

@Path("eHospital")
public class EHospitalApi {

    static private final Logger logger = LoggerFactory.getLogger(EHospitalApi.class);

    private final CordaRPCOps rpcOps;
    private final CordaX500Name myLegalName;

    public EHospitalApi(CordaRPCOps rpcOps) {
        this.rpcOps = rpcOps;
        this.myLegalName = rpcOps.nodeInfo().getLegalIdentities().get(0).getName();
    }

    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, CordaX500Name> myIdentity() {
        return ImmutableMap.of("me", rpcOps.nodeInfo().getLegalIdentities().get(0).getName());
    }

    @GET
    @Path("getAllPatientDetails")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<PatientDataState>> getAllPatientDetails() {
        return rpcOps.vaultQuery(PatientDataState.class).getStates();
    }

    @GET
    @Path("trackPatientData")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<PatientDataState>> trackPatientData(@QueryParam("id") String idString) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(idString);
        List<UniqueIdentifier> linearIds = new ArrayList<>();
        linearIds.add(linearId);

        QueryCriteria linearCriteriaAll = new QueryCriteria.LinearStateQueryCriteria(null,
                linearIds,
                Vault.StateStatus.ALL,
                null);

        return rpcOps.vaultQueryByCriteria(linearCriteriaAll, PatientDataState.class).getStates();
    }

    @GET
    @Path("readPatientData")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response readPatientData(@QueryParam("id") String idString, @QueryParam("port") String port) {
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(idString);
        List<UniqueIdentifier> linearIds = new ArrayList<>();
        linearIds.add(linearId);

        QueryCriteria linearCriteriaAll = new QueryCriteria.LinearStateQueryCriteria(null,
                linearIds,
                Vault.StateStatus.ALL,
                null);

        PatientDataState patientDataState = rpcOps.vaultQueryByCriteria(linearCriteriaAll, PatientDataState.class)
                .getStates().get(0).getState().getData();

        SecureHash secureHash = patientDataState.getAttachmentHashValue();
        try {
            if (rpcOps.attachmentExists(secureHash)) {
                /**Once uploaded the file can be downloaded from http://localhost:port/attachments/secure-hash URL
                 * If we wish to share with any other Hospital we can download the file and send the createPatientData
                 * request to the other Hospital*/
                URL url = new URL("http://localhost:" + port + "/attachments/" + secureHash.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                if (connection.getResponseCode() != SC_OK) {
                    System.out.println("Issue in connection");
                }
                String readStream = readStream(connection.getInputStream());
                FileOutputStream fos = new FileOutputStream("F:\\");
                fos.write(readStream.getBytes());
                fos.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        final String msg = String.format("File saved to path");
        return Response.status(CREATED).entity(msg).build();
    }


    @GET
    @Path("createPatientData")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPatientData(@QueryParam("hospitalName") String hospitalName,
                                      @QueryParam("userId") String userId) {
        Party owner = rpcOps.partiesFromName(hospitalName, false).iterator().next();
        InputStream in = null;
        File file = null;
        SecureHash attachmentHashValue = null;
        try {
            file = new File("F:\\testfile.zip");
            URL newFileURL = file.toURI().toURL();
            //java.io.BufferedInputStream will be created by openStream()
            in = newFileURL.openStream();
            attachmentHashValue = rpcOps.uploadAttachment(in);
            System.out.print("attachmentHashValue: " + attachmentHashValue.toString() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(CreatePatientDataFlow.class,
                    owner,
                    userId,
                    attachmentHashValue).getReturnValue().get();

            System.out.println("\nPatientDataState created with transaction id: " + signedTx.getId()
                    + " and linear id: " + signedTx.getCoreTransaction().outputsOfType(PatientDataState.class).get(0).getLinearId()
                    + " and hash: " + attachmentHashValue);

            final String msg = String.format("Linear Id: %s\n",
                    signedTx.getCoreTransaction().outputsOfType(PatientDataState.class).get(0).getLinearId()
                            + " and attachment hash value:" + attachmentHashValue);
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    private static String readStream(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {
            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
