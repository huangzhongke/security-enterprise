/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.oss.cloud;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.renren.common.exception.ErrorCode;
import io.renren.common.exception.RenException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * MinIO 存储
 *
 * @author Mark sunlightcs@gmail.com
 */
public class MinioCloudStorageService extends AbstractCloudStorageService {
    private MinioClient minioClient;

    public MinioCloudStorageService(CloudStorageConfig config){
        this.config = config;
        //初始化
        init();
    }

    private void init(){
        try {
            minioClient = new MinioClient(config.getMinioEndPoint(), config.getMinioAccessKey(), config.getMinioSecretKey());
        } catch (InvalidEndpointException e) {
            e.printStackTrace();
        } catch (InvalidPortException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path);
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            //如果BucketName不存在，则创建
            boolean found = minioClient.bucketExists(config.getMinioBucketName());
            if (!found) {
                minioClient.makeBucket(config.getMinioBucketName());
            }

            minioClient.putObject(config.getMinioBucketName(), path, inputStream, Long.valueOf(inputStream.available()),
                    null, null, "application/octet-stream");
        } catch (Exception e) {
            throw new RenException(ErrorCode.OSS_UPLOAD_FILE_ERROR, e, "");
        }

        return config.getMinioEndPoint() + "/" + config.getMinioBucketName() + "/" + path;
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getMinioPrefix(), suffix));
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getMinioPrefix(), suffix));
    }
}
