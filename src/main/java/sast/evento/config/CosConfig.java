package sast.evento.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/3 15:24
 */
@Configuration
public class CosConfig {
    public static String secretId;
    public static String secretKey;
    public static String COS_REGION;
    public static String bucketName;
    @Value("${cos.secretId}")
    private String secretIdValue;
    @Value("${cos.secretKey}")
    private String secretKeyValue;
    @Value("${cos.cosRegion}")
    private String cosRegion;
    @Value("${cos.bucketName}")
    private String bucketNameValue;

    @PostConstruct
    private void config() {
        secretId = secretIdValue;
        secretKey = secretKeyValue;
        COS_REGION = cosRegion;
        bucketName = bucketNameValue;
    }

}
