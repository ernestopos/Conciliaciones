export interface PresignedUploadResponse{
    bucketName: string;
    objectKey: string;
    presignedUrl: string;
    expiresInMinutes: number;
}