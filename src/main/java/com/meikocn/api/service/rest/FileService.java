package com.meikocn.api.service.rest;

import com.meikocn.api.dto.rest.request.FileReqDto;
import com.meikocn.api.dto.rest.response.FileVersionResDto;
import com.meikocn.api.exception.ConflictException;
import com.meikocn.api.mapping.provider.S3Mapper;
import com.meikocn.api.mapping.rest.FileMapper;
import com.meikocn.api.model.File;
import com.meikocn.api.repository.FileRepository;
import com.meikocn.api.service.provider.S3Service;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.ObjectVersion;

@Service
public class FileService extends BaseService<File, FileRepository> {

  private final FileMapper fileMapper;
  private final S3Service s3Service;
  private final S3Mapper s3Mapper;

  public FileService(
      FileRepository repository, FileMapper fileMapper, S3Service s3Service, S3Mapper s3Mapper) {
    super(repository);
    this.fileMapper = fileMapper;
    this.s3Service = s3Service;
    this.s3Mapper = s3Mapper;
  }

  public File create(FileReqDto dto) {
    if (repository.findByKeyIgnoreCase(dto.getKey()).isPresent()) {
      throw new ConflictException(modelClass, "key", dto.getKey());
    }
    File file = fileMapper.dto2Model(dto);
    return repository.save(file);
  }

  public File updateById(UUID id, FileReqDto dto) {
    File file = this.getById(id, false);
    if (!file.getKey().equalsIgnoreCase(dto.getKey())) {
      if (repository.findByKeyIgnoreCase(dto.getKey()).isPresent()) {
        throw new ConflictException(modelClass, "key", dto.getKey());
      }
    }
    fileMapper.updateModelFromDto(dto, file);
    return repository.save(file);
  }

  public List<FileVersionResDto> getFileVersions(String key) {
    List<ObjectVersion> objectVersions = s3Service.getFileVersions(key);
    List<FileVersionResDto> resDtos = s3Mapper.objectVersions2Dto(objectVersions);
    Map<String, Map<String, String>> metadataMap = s3Service.getMetadata(objectVersions);
    resDtos.forEach(
        resDto -> {
          Map<String, String> metadata = metadataMap.get(resDto.getVersionId());
          resDto.setModifiedBy(metadata.get("modified-by"));
        });
    return resDtos;
  }
}
