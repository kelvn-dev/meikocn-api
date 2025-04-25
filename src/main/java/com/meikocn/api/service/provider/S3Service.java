package com.meikocn.api.service.provider;

import com.meikocn.api.config.AWSPropConfig;
import com.meikocn.api.enums.ContentDisposition;
import com.meikocn.api.exception.BaseException;
import com.meikocn.api.utils.HelperUtils;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.ObjectVersion;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
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
  private static final int PRESIGNED_URL_TTL_IN_MIN = 10;

  /** For uploading */
  public PresignedPutObjectRequest getPresignedUrl(
      JwtAuthenticationToken token, String contentType, ObjectCannedACL acl) {
    try {
      String randomString = HelperUtils.getRandomString();
      String key = String.format("%s/%s", awsPropConfig.getS3().getPrefix(), randomString);

      PutObjectRequest objectRequest =
          PutObjectRequest.builder()
              .bucket(awsPropConfig.getS3().getBucket())
              .key(key)
              .contentType(contentType)
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

  /** For retrieving specific version */
  public PresignedGetObjectRequest getPresignedUrl(
      String key, String versionId, ContentDisposition contentDisposition) {
    try {
      GetObjectRequest.Builder getObjectRequestBuilder =
          GetObjectRequest.builder()
              .bucket(awsPropConfig.getS3().getBucket())
              .key(key)
              .responseContentDisposition(
                  ContentDisposition.ATTACHMENT.equals(contentDisposition)
                      ? "attachment; filename=\"" + key.concat(".png") + "\""
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

  /** List all versions of a file */
  public List<ObjectVersion> listFileVersions(String key) {
    try {
      ListObjectVersionsRequest request =
          ListObjectVersionsRequest.builder()
              .bucket(awsPropConfig.getS3().getBucket())
              .prefix(key)
              .build();

      ListObjectVersionsResponse response = s3Client.listObjectVersions(request);
      return response.versions();

      //      return response.versions().stream()
      //          .map(version -> {
      //            // Get metadata for each version
      //            HeadObjectRequest headRequest = HeadObjectRequest.builder()
      //                .bucket(awsPropConfig.getS3().getBucket())
      //                .key(version.key())
      //                .versionId(version.versionId())
      //                .build();
      //
      //            HeadObjectResponse headResponse = s3Client.headObject(headRequest);
      //            Map<String, String> metadata = headResponse.metadata();
      //
      //            return new FileVersionInfo(
      //                version.key(),
      //                version.versionId(),
      //                version.lastModified(),
      //                version.isLatest(),
      //                metadata.get("modified-by")
      //            );
      //          })
      //          .collect(Collectors.toList());
    } catch (S3Exception e) {
      throw new BaseException(e.getMessage());
    }
  }
}
