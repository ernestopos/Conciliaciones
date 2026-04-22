package com.conciliaciones.reconciliation.core.application.port.out.storage;

import com.conciliaciones.reconciliation.core.domain.model.storage.StoredObject;
import java.util.List;

public interface ObjectStoragePort {

    void createBucket(String bucketName);

    void deleteBucket(String bucketName);

    List<String> listBuckets();

    boolean bucketExists(String bucketName);

    String generatePresignedUploadUrl(String bucketName, String objectKey, String contentType);

    void moveObject(String sourceBucketName, String sourceKey, String targetBucketName, String targetKey);

    List<StoredObject> listObjects(String bucketName, String prefix);

    void uploadObject(String bucketName, String objectKey, byte[] content, String contentType);

    void updateObject(String bucketName, String objectKey, byte[] content, String contentType);

    void deleteObject(String bucketName, String objectKey);
}
