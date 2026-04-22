package com.conciliaciones.msfilemanagement.application.usecase.storage;

import com.conciliaciones.msfilemanagement.application.port.in.storage.ListBucketsUseCase;
import com.conciliaciones.msfilemanagement.application.port.out.storage.ObjectStoragePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListBucketsService implements ListBucketsUseCase {

    private final ObjectStoragePort objectStoragePort;

    @Override
    public List<String> list() {
        return objectStoragePort.listBuckets();
    }
}
