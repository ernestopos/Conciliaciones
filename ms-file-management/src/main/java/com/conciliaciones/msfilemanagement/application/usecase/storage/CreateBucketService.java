package com.conciliaciones.msfilemanagement.application.usecase.storage;

import com.conciliaciones.msfilemanagement.application.port.in.storage.CreateBucketUseCase;
import com.conciliaciones.msfilemanagement.application.port.out.storage.ObjectStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateBucketService implements CreateBucketUseCase {

    private final ObjectStoragePort objectStoragePort;
    private final BucketNameGenerator bucketNameGenerator;

    @Override
    public String create(String bucketName) {
        String resolvedName = bucketName == null || bucketName.isBlank()
                ? bucketNameGenerator.generate()
                : bucketName.toLowerCase();
        objectStoragePort.createBucket(resolvedName);
        return resolvedName;
    }
}
