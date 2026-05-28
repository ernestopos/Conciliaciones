package com.conciliaciones.msmanagementtask.application.port.out.storage;

import com.conciliaciones.msmanagementtask.domain.model.storage.StoredObject;
import com.conciliaciones.msmanagementtask.domain.model.storage.StoredObjectContent;
import java.util.List;

public interface ObjectStoragePort {

    void createBucket(String bucketName);

    List<String> listBuckets();

    boolean bucketExists(String bucketName);

    boolean objectExists(String bucketName, String objectKey);

    StoredObjectContent readObject(String bucketName, String objectKey);

    List<StoredObject> listObjects(String bucketName, String prefix);

    void deleteObject(String bucketName, String objectKey);
}
