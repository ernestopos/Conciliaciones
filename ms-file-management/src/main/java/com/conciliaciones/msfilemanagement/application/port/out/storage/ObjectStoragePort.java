package com.conciliaciones.msfilemanagement.application.port.out.storage;

import com.conciliaciones.msfilemanagement.domain.model.storage.StoredObject;
import java.util.List;

public interface ObjectStoragePort {

    void createBucket(String bucketName);

    List<String> listBuckets();

    boolean bucketExists(String bucketName);

    String generatePresignedUploadUrl(String bucketName, String objectKey, String contentType);

    List<StoredObject> listObjects(String bucketName, String prefix);

    void deleteObject(String bucketName, String objectKey);
}
