package sast.evento.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import sast.sastlink.sdk.service.SastLinkService;
import sast.sastlink.sdk.service.impl.RestTemplateSastLinkService;

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
    @Value("${sast-link-web.code-verifier-web}")
    private String codeVerifier_web;
    @Value("${sast-link-web.link-path-web}")
    private String linkPath_web;

    @Bean
    public SastLinkService sastLinkService() {
        return RestTemplateSastLinkService.Builder()
                .setRedirectUri(redirectUri)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setCodeVerifier(codeVerifier)
                .setHostName(linkPath)
                .build();
    }

    @Bean
    public SastLinkService sastLinkServiceWeb() {
        return RestTemplateSastLinkService.Builder()
                .setRedirectUri(redirectUri_web)
                .setClientId(clientId_web)
                .setClientSecret(clientSecret_web)
                .setCodeVerifier(codeVerifier_web)
                .setHostName(linkPath_web)
                .build();
    }
}
