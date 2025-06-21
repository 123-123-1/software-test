package com.tongji.sportmanagement.Common;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CopyObjectRequest;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class OssService
{
  OSS ossClient;
  String bucketName;

  @PostConstruct
  public void init(){
    try{

       String endpoint = "oss-cn-shanghai.aliyuncs.com";
       String accessKeyId = "LTAI5t9A4M3j8pNFQ18aaWRo";
       String accessKeySecret = "OPTi7VshJxtojfHqWvxQdzthG65V7T";
       bucketName = "hkf123";

      ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
    catch(Exception e){
      System.err.println("找不到oss-key.txt");
    }
  }

  public void uploadFile(InputStream fileInput, String key){
    PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, fileInput);
    ossClient.putObject(putObjectRequest);
  }

  public void deleteFile(String key){
    ossClient.deleteObject(bucketName, key);
  }

  public String getFileLink(String key){
    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key);
    request.setMethod(HttpMethod.GET); // 设置HTTP方法为GET
    request.setExpiration(new Date(System.currentTimeMillis() + 3600*1000)); // 设置URL过期时间为当前时间1小时后
    URL url = ossClient.generatePresignedUrl(request); // 生成URL
    return url.toString();
  }

  public void copyDefault(String sourceName, String newName){
    CopyObjectRequest request = new CopyObjectRequest(bucketName, sourceName, bucketName, newName);
    ossClient.copyObject(request);
  }

  @PreDestroy
  public void destroy(){
    if(ossClient != null){
      ossClient.shutdown();
    }
  }
}
