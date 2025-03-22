package com.urlino.urlino.Bigtable;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class BigtableConfig {
    @Value("${spring.cloud.gcp.bigtable.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.bigtable.instance-id}")
    private String instanceId;

    @Bean(destroyMethod = "close")
    public BigtableDataClient bigtableDataClient() throws IOException {
        return BigtableDataClient.create(projectId, instanceId);
    }
}
