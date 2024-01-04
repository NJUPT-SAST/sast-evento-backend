package sast.evento.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import fun.feellmoose.service.SastLinkService;
import fun.feellmoose.test.TestSastLinkServiceAdapter;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/23 22:30
 */
@Configuration
public class SastLinkServiceConfig {
    @Value("${sast-link.redirect-uri}")
    private String redirectUri;
    @Value("${sast-link.client-id}")
    private String clientId;
    @Value("${sast-link.client-secret}")
    private String clientSecret;
    @Value("${sast-link.code-verifier}")
    private String codeVerifier;
    @Value("${sast-link.link-path}")
    private String linkPath;

    @Value("${sast-link-web.redirect-uri-web}")
    private String redirectUri_web;
    @Value("${sast-link-web.client-id-web}")
    private String clientId_web;
    @Value("${sast-link-web.client-secret-web}")
    private String clientSecret_web;

    @Value("${sast-link-mobile-dev.redirect-uri-mobile-dev}")
    private String redirectUri_mobile_dev;
    @Value("${sast-link-mobile-dev.client-id-mobile-dev}")
    private String clientId_mobile_dev;
    @Value("${sast-link-mobile-dev.client-secret-mobile-dev}")
    private String clientSecret_mobile_dev;

    @Bean
    public SastLinkService sastLinkService() {
        return new TestSastLinkServiceAdapter.Builder()
                .setRedirectUri(redirectUri)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setCodeVerifier(codeVerifier)
                .setHostName(linkPath)
                .build();
    }

    @Bean
    public SastLinkService sastLinkServiceWeb() {
        return new TestSastLinkServiceAdapter.Builder()
                .setRedirectUri(redirectUri_web)
                .setClientId(clientId_web)
                .setClientSecret(clientSecret_web)
                .setCodeVerifier(codeVerifier)
                .setHostName(linkPath)
                .build();
    }

    @Bean
    public SastLinkService sastLinkServiceMobileDev() {
        return new TestSastLinkServiceAdapter.Builder()
                .setRedirectUri(redirectUri_mobile_dev)
                .setClientId(clientId_mobile_dev)
                .setClientSecret(clientSecret_mobile_dev)
                .setCodeVerifier(codeVerifier)
                .setHostName(linkPath)
                .build();
    }
}
