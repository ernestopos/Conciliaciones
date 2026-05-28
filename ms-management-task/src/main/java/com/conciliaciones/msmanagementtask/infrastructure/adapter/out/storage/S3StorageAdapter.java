package com.conciliaciones.msmanagementtask.infrastructure.adapter.out.storage;

import com.conciliaciones.msmanagementtask.application.port.out.storage.ObjectStoragePort;
import com.conciliaciones.msmanagementtask.domain.model.storage.StoredObject;
import com.conciliaciones.msmanagementtask.domain.model.storage.StoredObjectContent;
import com.conciliaciones.msmanagementtask.infrastructure.config.AwsS3Properties;
import com.conciliaciones.msmanagementtask.infrastructure.exception.ObjectStorageException;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CORSConfiguration;
import software.amazon.awssdk.services.s3.model.CORSRule;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.aws.s3", name = "enabled", havingValue = "true", matchIfMissing = true)
public class S3StorageAdapter implements ObjectStoragePort {

    private static final List<String> ALLOWED_ORIGINS = List.of("http://localhost:4200");

    private final S3Client s3Client;
    private final AwsS3Properties properties;

    @Override
    public void createBucket(String bucketName) {
        String resolvedBucket = resolveBucket(bucketName);
        try {
            log.info("LOG INICIO X = createBucket {}", resolvedBucket);

            if (!bucketExists(resolvedBucket)) {
                s3Client.createBucket(CreateBucketRequest.builder()
                        .bucket(resolvedBucket)
                        .build());
                log.info("Bucket creado correctamente: {}", resolvedBucket);
            } else {
                log.info("Bucket ya existe: {}", resolvedBucket);
            }

            applyCors(resolvedBucket);

            log.info("LOG FIN X = createBucket {}", resolvedBucket);
        } catch (BucketAlreadyOwnedByYouException | BucketAlreadyExistsException ex) {
            log.info("Bucket ya existe: {}", resolvedBucket);
            applyCors(resolvedBucket);
        } catch (Exception ex) {
            throw new ObjectStorageException("Error creando bucket " + resolvedBucket, ex);
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
            HeadBucketResponse ignored = s3Client.headBucket(
                    HeadBucketRequest.builder()
                            .bucket(resolvedBucket)
                            .build()
            );
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
    public boolean objectExists(String bucketName, String objectKey) {
        String resolvedBucket = resolveBucket(bucketName);
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(resolvedBucket)
                    .key(objectKey)
                    .build());
            return true;
        } catch (NoSuchKeyException ex) {
            return false;
        } catch (S3Exception ex) {
            if (ex.statusCode() == 404) {
                return false;
            }
            throw new ObjectStorageException("Error validando objeto S3 bucket=" + resolvedBucket + " key=" + objectKey, ex);
        }
    }

    @Override
    public StoredObjectContent readObject(String bucketName, String objectKey) {
        String resolvedBucket = resolveBucket(bucketName);
        try {
            log.info("LOG INICIO X = readObject bucket={} key={}", resolvedBucket, objectKey);

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(resolvedBucket)
                    .key(objectKey)
                    .build());

            GetObjectResponse response = objectBytes.response();

            return new StoredObjectContent(
                    resolvedBucket,
                    objectKey,
                    objectBytes.asByteArray(),
                    response.contentType(),
                    response.contentLength(),
                    response.eTag()
            );
        } catch (NoSuchKeyException ex) {
            throw new ObjectStorageException("No existe objeto S3 bucket=" + resolvedBucket + " key=" + objectKey, ex);
        } catch (Exception ex) {
            throw new ObjectStorageException("Error leyendo objeto S3 bucket=" + resolvedBucket + " key=" + objectKey, ex);
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

    private void applyCors(String bucketName) {
        try {
            log.info("LOG INICIO X = applyCors {}", bucketName);

            CORSRule corsRule = CORSRule.builder()
                    .allowedOrigins(ALLOWED_ORIGINS)
                    .allowedMethods("PUT", "GET", "HEAD", "POST", "DELETE")
                    .allowedHeaders("*")
                    .exposeHeaders("ETag")
                    .maxAgeSeconds(3000)
                    .build();

            CORSConfiguration corsConfiguration = CORSConfiguration.builder()
                    .corsRules(corsRule)
                    .build();

            s3Client.putBucketCors(PutBucketCorsRequest.builder()
                    .bucket(bucketName)
                    .corsConfiguration(corsConfiguration)
                    .build());

            log.info("CORS aplicado correctamente al bucket: {}", bucketName);
            log.info("LOG FIN X = applyCors {}", bucketName);
        } catch (Exception ex) {
            throw new ObjectStorageException("Error aplicando CORS al bucket " + bucketName, ex);
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
}
