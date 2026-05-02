package com.conciliaciones.reconciliation.core.infrastructure.adapter.out.storage;

import com.conciliaciones.reconciliation.core.application.port.out.storage.ObjectStoragePort;
import com.conciliaciones.reconciliation.core.domain.model.storage.StoredObject;
import com.conciliaciones.reconciliation.core.infrastructure.config.AwsS3Properties;
import com.conciliaciones.reconciliation.core.infrastructure.exception.ObjectStorageException;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.aws.s3", name = "enabled", havingValue = "true", matchIfMissing = true)
public class S3StorageAdapter implements ObjectStoragePort {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AwsS3Properties properties;

    @Override
    public void createBucket(String bucketName) {
        String resolvedBucket = resolveBucket(bucketName);
        try {
            log.info("LOG INICIO X = createBucket {}", resolvedBucket);
            s3Client.createBucket(CreateBucketRequest.builder().bucket(resolvedBucket).build());
        } catch (BucketAlreadyOwnedByYouException | BucketAlreadyExistsException ex) {
            log.info("Bucket ya existe: {}", resolvedBucket);
        } catch (Exception ex) {
            throw new ObjectStorageException("Error creando bucket " + resolvedBucket, ex);
        }
    }

    @Override
    public void deleteBucket(String bucketName) {
        String resolvedBucket = resolveBucket(bucketName);
        try {
            log.info("LOG INICIO X = deleteBucket {}", resolvedBucket);
            s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(resolvedBucket).build());
        } catch (Exception ex) {
            throw new ObjectStorageException("Error eliminando bucket " + resolvedBucket, ex);
        }
    }

    @Override
    public List<String> listBuckets() {
        try {
            log.info("LOG INICIO X = listBuckets");
            ListBucketsResponse response = s3Client.listBuckets();
            return response.buckets().stream().map(bucket -> bucket.name()).toList();
        } catch (Exception ex) {
            throw new ObjectStorageException("Error listando buckets", ex);
        }
    }

    @Override
    public boolean bucketExists(String bucketName) {
        String resolvedBucket = resolveBucket(bucketName);
        try {
            HeadBucketResponse ignored = s3Client.headBucket(HeadBucketRequest.builder().bucket(resolvedBucket).build());
            return true;
        } catch (NoSuchBucketException ex) {
            return false;
        } catch (S3Exception ex) {
            if (ex.statusCode() == 404) {
                return false;
            }
            throw new ObjectStorageException("Error validando bucket " + resolvedBucket, ex);
        }
    }

    @Override
    public String generatePresignedUploadUrl(String bucketName, String objectKey, String contentType) {
        String resolvedBucket = resolveBucket(bucketName);
        try {
            log.info("LOG INICIO X = generatePresignedUploadUrl bucket={} key={}", resolvedBucket, objectKey);
            PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(properties.presignedUrlDurationMinutes()))
                    .putObjectRequest(put -> put.bucket(resolvedBucket).key(objectKey).contentType(contentType))
                    .build();
            return s3Presigner.presignPutObject(request).url().toExternalForm();
        } catch (Exception ex) {
            throw new ObjectStorageException("Error generando URL prefirmada para " + objectKey, ex);
        }
    }

    @Override
    public void moveObject(String sourceBucketName, String sourceKey, String targetBucketName, String targetKey) {
        String sourceBucket = resolveBucket(sourceBucketName);
        String targetBucket = resolveBucket(targetBucketName);
        try {
            log.info("LOG INICIO X = moveObject {}:{} -> {}:{}", sourceBucket, sourceKey, targetBucket, targetKey);
            s3Client.copyObject(CopyObjectRequest.builder()
                    .sourceBucket(sourceBucket)
                    .sourceKey(sourceKey)
                    .destinationBucket(targetBucket)
                    .destinationKey(targetKey)
                    .build());
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(sourceBucket)
                    .key(sourceKey)
                    .build());
        } catch (Exception ex) {
            throw new ObjectStorageException("Error moviendo objeto " + sourceKey, ex);
        }
    }

    @Override
    public List<StoredObject> listObjects(String bucketName, String prefix) {
        String resolvedBucket = resolveBucket(bucketName);
        try {
            log.info("LOG INICIO X = listObjects bucket={} prefix={}", resolvedBucket, prefix);
            return s3Client.listObjectsV2(ListObjectsV2Request.builder()
                            .bucket(resolvedBucket)
                            .prefix(prefix)
                            .build())
                    .contents()
                    .stream()
                    .map(object -> new StoredObject(
                            resolvedBucket,
                            object.key(),
                            object.size(),
                            object.lastModified(),
                            object.eTag()))
                    .toList();
        } catch (Exception ex) {
            throw new ObjectStorageException("Error listando objetos del bucket " + resolvedBucket, ex);
        }
    }

    @Override
    public void uploadObject(String bucketName, String objectKey, byte[] content, String contentType) {
        putObject(bucketName, objectKey, content, contentType, false);
    }

    @Override
    public void updateObject(String bucketName, String objectKey, byte[] content, String contentType) {
        putObject(bucketName, objectKey, content, contentType, true);
    }

    @Override
    public void deleteObject(String bucketName, String objectKey) {
        String resolvedBucket = resolveBucket(bucketName);
        try {
            log.info("LOG INICIO X = deleteObject bucket={} key={}", resolvedBucket, objectKey);
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(resolvedBucket)
                    .key(objectKey)
                    .build());
        } catch (Exception ex) {
            throw new ObjectStorageException("Error eliminando objeto " + objectKey, ex);
        }
    }

    private void putObject(String bucketName, String objectKey, byte[] content, String contentType, boolean update) {
        String resolvedBucket = resolveBucket(bucketName);
        validateContent(content, objectKey);
        try {
            log.info("LOG INICIO X = {}Object bucket={} key={}", update ? "update" : "upload", resolvedBucket, objectKey);
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(resolvedBucket)
                            .key(objectKey)
                            .contentType(contentType)
                            .contentLength((long) content.length)
                            .build(),
                    RequestBody.fromBytes(content));
        } catch (Exception ex) {
            throw new ObjectStorageException("Error guardando objeto " + objectKey, ex);
        }
    }

    private String resolveBucket(String bucketName) {
        if (bucketName != null && !bucketName.isBlank()) {
            return bucketName;
        }
        if (properties.defaultBucket() == null || properties.defaultBucket().isBlank()) {
            throw new ObjectStorageException("No se recibió bucket y no existe app.aws.s3.default-bucket configurado");
        }
        return properties.defaultBucket();
    }

    private void validateContent(byte[] content, String objectKey) {
        if (content == null || content.length == 0) {
            throw new ObjectStorageException("El contenido del objeto no puede ser nulo o vacío: " + objectKey);
        }
    }
}
