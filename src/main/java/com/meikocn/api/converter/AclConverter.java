package com.meikocn.api.converter;

import com.meikocn.api.exception.BadRequestException;
import org.springframework.core.convert.converter.Converter;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

public class AclConverter implements Converter<String, ObjectCannedACL> {
  @Override
  public ObjectCannedACL convert(String source) {
    try {
      return ObjectCannedACL.valueOf(source.toUpperCase().replace('-', '_'));
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Invalid ACL");
    }
  }
}
