package com.meikocn.api.service.provider;

import com.meikocn.api.config.AWSPropConfig;
import com.meikocn.api.enums.ContentDisposition;
import com.meikocn.api.exception.BaseException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

  private final AWSPropConfig awsPropConfig;
  private final S3Presigner s3Presigner;
  private final S3Client s3Client;
  private final Executor s3TaskExecutor;
  private static final int PRESIGNED_URL_TTL_IN_MIN = 10;

  /**
   * Presigned url for uploading file
   *
   * @param token
   * @param key
   * @param acl
   * @return
   */
  public PresignedPutObjectRequest getPresignedUrl(
      JwtAuthenticationToken token, String key, ObjectCannedACL acl) {
    try {
      PutObjectRequest objectRequest =
          PutObjectRequest.builder()
              .bucket(awsPropConfig.getS3().getBucket())
              .key(String.format("%s/%s", awsPropConfig.getS3().getPrefix(), key))
              .acl(acl)
              .metadata(Map.of("modified-by", token.getToken().getSubject()))
              .build();

      PutObjectPresignRequest presignRequest =
          PutObjectPresignRequest.builder()
              .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_TTL_IN_MIN))
              .putObjectRequest(objectRequest)
              .build();

      return s3Presigner.presignPutObject(presignRequest);
    } catch (S3Exception e) {
      throw new BaseException(e.getMessage());
    }
  }

  /**
   * Presigned url for retrieving specific version
   *
   * @param key
   * @param versionId
   * @param contentDisposition
   * @return
   */
  public PresignedGetObjectRequest getPresignedUrl(
      String key, String versionId, ContentDisposition contentDisposition) {
    try {
      GetObjectRequest.Builder getObjectRequestBuilder =
          GetObjectRequest.builder()
              .bucket(awsPropConfig.getS3().getBucket())
              .key(String.format("%s/%s", awsPropConfig.getS3().getPrefix(), key))
              .responseContentDisposition(
                  ContentDisposition.ATTACHMENT.equals(contentDisposition)
                      ? "attachment; filename=\"" + key + "\""
                      : ContentDisposition.INLINE.toString());

      if (versionId != null) {
        getObjectRequestBuilder.versionId(versionId);
      }

      GetObjectRequest getObjectRequest = getObjectRequestBuilder.build();

      GetObjectPresignRequest getObjectPresignRequest =
          GetObjectPresignRequest.builder()
              .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_TTL_IN_MIN))
              .getObjectRequest(getObjectRequest)
              .build();

      return s3Presigner.presignGetObject(getObjectPresignRequest);
    } catch (S3Exception e) {
      throw new BaseException(e.getMessage());
    }
  }

  /**
   * List all versions of a file
   *
   * @param key
   * @return
   */
  public List<ObjectVersion> getFileVersions(String key) {
    try {
      ListObjectVersionsRequest request =
          ListObjectVersionsRequest.builder()
              .bucket(awsPropConfig.getS3().getBucket())
              .prefix(String.format("%s/%s", awsPropConfig.getS3().getPrefix(), key))
              .build();

      ListObjectVersionsResponse response = s3Client.listObjectVersions(request);
      return response.versions();
    } catch (S3Exception e) {
      throw new BaseException(e.getMessage());
    }
  }

  /**
   * Get metadata of an object version
   *
   * @param objectVersion
   * @return
   */
  public Map<String, String> getMetadata(ObjectVersion objectVersion) {
    HeadObjectRequest headRequest =
        HeadObjectRequest.builder()
            .bucket(awsPropConfig.getS3().getBucket())
            .key(objectVersion.key())
            .versionId(objectVersion.versionId())
            .build();

    HeadObjectResponse headObjectResponse = s3Client.headObject(headRequest);
    return headObjectResponse.metadata();
  }

  /**
   * Get metadata map of list object version
   *
   * @param objectVersions
   * @return Map<versionId, metadata>
   */
  public Map<String, Map<String, String>> getMetadata(List<ObjectVersion> objectVersions) {
    ConcurrentHashMap<String, Map<String, String>> resultMap = new ConcurrentHashMap<>();

    List<CompletableFuture<Void>> futures =
        objectVersions.stream()
            .map(
                objectVersion ->
                    CompletableFuture.runAsync(
                        () -> {
                          Map<String, String> metadata = this.getMetadata(objectVersion);
                          resultMap.put(objectVersion.versionId(), metadata);
                        },
                        s3TaskExecutor))
            .toList();

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    return resultMap;
  }
}
