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
    @Value("${sast-link.link-host}")
    private String linkHostName;

    @Bean
    public SastLinkService sastLinkService(RestTemplate restTemplate) {
        return RestTemplateSastLinkService.Builder()
                .setRedirectUri(redirectUri)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setCodeVerifier(codeVerifier)
                .setHostName(linkHostName)
                .setRestTemplate(restTemplate)
                .build();
    }
}
