package com.landRegistry.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.landRegistry.api.PropertyTransferApi;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.webserver.services.WebServerPluginRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PropertyPlugin implements WebServerPluginRegistry {
    private final List<Function<CordaRPCOps, ?>> webApis = ImmutableList.of(PropertyTransferApi::new);

    private final Map<String, String> staticServeDirs =
            ImmutableMap.of(
            "landDepartment", getClass().getClassLoader().getResource("landDepartmentWeb").toExternalForm()
    );

    @Override @NotNull public List<Function<CordaRPCOps, ?>> getWebApis() { return webApis; }
    @Override @NotNull public Map<String, String> getStaticServeDirs() { return staticServeDirs; }
    @Override public void customizeJSONSerialization(ObjectMapper om) { }
}