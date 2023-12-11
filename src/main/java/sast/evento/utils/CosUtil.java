package sast.evento.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
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

    /* 上传图片，同一张图片同一个名字会覆盖，防止重复上传 */
    public static String upload(MultipartFile file, String dir) throws IOException, NoSuchAlgorithmException, CosClientException {
        String dirPrefix = dir.isEmpty() ? "" : (dir + "/");
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("size of file is out of limit 10M.");
        }
        /* get key */
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("name of upload file is empty.");
        }
        if(!originalFileName.contains(".")){
            throw new IllegalArgumentException("error originalFileName");
        }
        int idx = originalFileName.lastIndexOf(".");
        String prefix = originalFileName.substring(0, idx);
        prefix = prefix.length() < 3 ? prefix + "_file" : prefix;
        String key = null;
        // TODO compress image & 防瑟瑟
        /* upload */
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
//        InputStream inputStream = file.getInputStream();
//        key = dirPrefix + prefix + "_" + md5HashCode(inputStream);
        key = dirPrefix + prefix + "_" + System.currentTimeMillis();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
        putObjectRequest.setStorageClass(StorageClass.Standard);
        cosClient.putObject(putObjectRequest);
        return key;
    }

    /* 通过上一次获得的最后一个URL来获取下一个分页 */
    public static List<String> getURLs(String dir, String lastURL, Integer size) throws CosClientException {
        String dirPrefix = dir.isEmpty() ? "" : (dir + "/");
        String lastKey = lastURL.isEmpty() ? "" : lastURL.substring(lastURL.indexOf("/", 9) + 1);
        ListObjectsRequest request = new ListObjectsRequest(bucketName, dirPrefix, lastKey, "/", size);
        ObjectListing objectListing = cosClient.listObjects(request);
        List<COSObjectSummary> summaryList = objectListing.getObjectSummaries();
        return summaryList.stream()
                .map(COSObjectSummary::getKey)
                .map(key -> "https://" + bucketName + ".cos." + COS_REGION + ".myqcloud.com/" + key)
                .toList();
    }

    /* 获取当前目录下所有key(无限制) */
    public static List<String> getKeys(String dir) throws CosClientException {
        String dirPrefix = dir.isEmpty() ? "" : (dir + "/");
        List<String> res = new ArrayList<>();
        ObjectListing objectListing;
        String marker = null;
        do {
            ListObjectsRequest request = new ListObjectsRequest(bucketName, dirPrefix, marker, "/", 1000);
            objectListing = cosClient.listObjects(request);
            List<String> every = objectListing.getObjectSummaries().stream()
                    .map(COSObjectSummary::getKey)
                    .toList();
            res.addAll(every);
            marker = objectListing.getNextMarker();
        } while (marker != null);
        return res;
    }

    public static List<String> changeKey2URL(List<String> keys) {
        return keys.stream()
                .map(key -> "https://" + bucketName + ".cos." + COS_REGION + ".myqcloud.com/" + key)
                .toList();
    }

    public static String changeKey2URL(String key){
        return "https://" + bucketName + ".cos." + COS_REGION + ".myqcloud.com/" + key;
    }

    /* 获取当前桶下1000以内所有目录 */
    public static List<String> getDirs(String dir) throws CosClientException {
        String dirPrefix = dir.isEmpty() ? "" : (dir + "/");
        ListObjectsRequest request = new ListObjectsRequest(bucketName, dirPrefix, "", "/", 1000);
        ObjectListing objectListing = cosClient.listObjects(request);
        return objectListing.getCommonPrefixes().stream()
                .map(s -> s.substring(0, s.length() - 1))
                .toList();
    }

    /* 根据url删除图片 */
    public static void deleteByUrl(String url) throws CosClientException {
        String key = url.substring(url.indexOf("/", 9) + 1);
        cosClient.deleteObject(bucketName, key);
    }

    /* 根据key删除图片 */
    public static void deleteByKey(String key) throws CosClientException {
        cosClient.deleteObject(bucketName, key);
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


    private static String md5HashCode(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[1024];
        int length = -1;
        while ((length = inputStream.read(buffer, 0, 1024)) != -1) {
            md.update(buffer, 0, length);
        }
        inputStream.close();
        byte[] md5Bytes = md.digest();
        BigInteger bigInt = new BigInteger(1, md5Bytes);
        return bigInt.toString(16);
    }
}