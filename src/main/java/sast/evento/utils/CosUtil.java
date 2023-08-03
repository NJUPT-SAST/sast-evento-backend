package sast.evento.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import org.springframework.web.multipart.MultipartFile;
import sast.evento.exception.LocalRunTimeException;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static sast.evento.config.CosConfig.*;


/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/2 22:10
 */
public class CosUtil {
    private static final COSClient cosClient = createCOSClient();

    //上传+查询+删除
    //对象键是必须品，增加name属性
    //返回url
    public static String upload(MultipartFile file) {
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("size of file is out of limit 10M.");
        }
        /* get key */
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("name of upload file is empty.");
        }
        int idx = originalFileName.lastIndexOf(".");
        String prefix = originalFileName.substring(0, idx);
        prefix = prefix.length() < 3 ? prefix + "_file" : prefix;
        String key = prefix + "_" + System.currentTimeMillis();
        /* upload */
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            putObjectRequest.setStorageClass(StorageClass.Standard);
            cosClient.putObject(putObjectRequest);
        } catch (CosClientException | IOException e) {
            throw new LocalRunTimeException(e.getMessage());
        }
        return "https://"+bucketName+".cos."+COS_REGION+".myqcloud.com/"+key;
    }

    public static void delete(String url) {
        String key = url.substring(url.lastIndexOf("/")+1);
        try {
            cosClient.deleteObject(bucketName, key);
        } catch (CosClientException e) {
            throw new LocalRunTimeException(e.getMessage());
        }
    }

    private static COSClient createCOSClient() {
        /* 由于上传的并发量和数量都的不多，这里不浪费资源，直接使用简单实例 */
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(new Region(COS_REGION));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(cred, clientConfig);
    }

    private static TransferManager createTransferManager() {
        /* 关于cos-sdk的使用 */
        /* https://cloud.tencent.com/document/product/436/65938 */
        COSClient cosClient = createCOSClient();
        ExecutorService threadPool = Executors.newFixedThreadPool(32);
        TransferManager transferManager = new TransferManager(cosClient, threadPool);
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(5 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(1 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);
        return transferManager;
    }


}
