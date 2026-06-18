package com.varunu28.centralservice.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private HttpComponentsClientHttpRequestFactory createFactory(int maxConnections) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxConnections);
        connectionManager.setDefaultMaxPerRoute(maxConnections);

        RequestConfig requestConfig = RequestConfig.custom()
                // If we can't get a connection from the pool instantly, throw an exception!
                .setConnectionRequestTimeout(Timeout.ZERO_MILLISECONDS)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    public RestClient serviceaRestClient(
            @Value("${servicea.url}") String serviceaUrl,
            @Value("${servicea.max-connections}") int maxConn
    ) {
        return RestClient.builder()
                .requestFactory(createFactory(maxConn))
                .baseUrl(serviceaUrl)
                .build();
    }

    @Bean
    public RestClient servicebRestClient(
            @Value("${serviceb.url}") String servicebUrl,
            @Value("${serviceb.max-connections}") int maxConn
    ) {
        return RestClient.builder()
                .requestFactory(createFactory(maxConn))
                .baseUrl(servicebUrl)
                .build();
    }
}
