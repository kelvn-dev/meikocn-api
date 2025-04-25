package com.meikocn.api.mapping.provider;

import com.meikocn.api.dto.provider.response.PresignedObjectRequestDto;
import com.meikocn.api.dto.rest.response.FileVersionResDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import software.amazon.awssdk.awscore.presigner.PresignedRequest;
import software.amazon.awssdk.services.s3.model.ObjectVersion;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Mapper(componentModel = "spring")
public interface S3Mapper {

  List<FileVersionResDto> objectVersions2Dto(List<ObjectVersion> objectVersion);

  @Mapping(target = "url", expression = "java( request.url().toString() )")
  @Mapping(target = "signedHeaders", source = "request", qualifiedByName = "signedHeaders")
  @Mapping(target = "expiration", expression = "java( request.expiration().toEpochMilli() )")
  PresignedObjectRequestDto putObjectRequest2Dto(PresignedPutObjectRequest request);

  @Mapping(target = "url", expression = "java( request.url().toString() )")
  @Mapping(target = "signedHeaders", source = "request", qualifiedByName = "signedHeaders")
  @Mapping(target = "expiration", expression = "java( request.expiration().toEpochMilli() )")
  PresignedObjectRequestDto getObjectRequest2Dto(PresignedGetObjectRequest request);

  @Named("signedHeaders")
  default Map<String, String> mapSignedHeaders(PresignedRequest request) {
    Map<String, List<String>> signedHeaders = request.signedHeaders();
    Map<String, String> mappedSignedHeaders = new HashMap<>();
    signedHeaders.forEach(
        (key, value) -> {
          if (key.startsWith("x-amz-")) {
            mappedSignedHeaders.put(key, !value.isEmpty() ? value.get(0) : null);
          }
        });
    return mappedSignedHeaders;
  }
}
