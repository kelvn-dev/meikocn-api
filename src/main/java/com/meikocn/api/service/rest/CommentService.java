package com.meikocn.api.service.rest;

import com.meikocn.api.dto.rest.request.CommentReqDto;
import com.meikocn.api.mapping.rest.CommentMapper;
import com.meikocn.api.model.Comment;
import com.meikocn.api.repository.CommentRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CommentService extends BaseService<Comment, CommentRepository> {

  private final CommentMapper commentMapper;

  public CommentService(CommentRepository repository, CommentMapper commentMapper) {
    super(repository);
    this.commentMapper = commentMapper;
  }

  public Comment create(CommentReqDto dto) {
    Comment comment = commentMapper.dto2Model(dto);
    return repository.save(comment);
  }

  public Comment updateById(UUID id, CommentReqDto dto) {
    Comment comment = this.getById(id, false);
    commentMapper.updateModelFromDto(dto, comment);
    return repository.save(comment);
  }
}
