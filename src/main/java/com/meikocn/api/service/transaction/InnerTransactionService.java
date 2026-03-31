package com.meikocn.api.service.transaction;

import com.meikocn.api.exception.BadRequestException;
import com.meikocn.api.exception.NotFoundException;
import com.meikocn.api.model.Project;
import com.meikocn.api.repository.ProjectRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InnerTransactionService {
  private final ProjectRepository projectRepository;

  @Transactional(propagation = Propagation.NESTED, isolation = Isolation.SERIALIZABLE)
  public void updateProject(TransactionDto dto) throws BadRequestException {
    String projectId = "097b29a3-7938-433b-a72a-0d56cb05c46f";
    Project project =
        projectRepository
            .findById(UUID.fromString(projectId))
            .orElseThrow(() -> new NotFoundException(Project.class, "id", projectId));

    project.setName(dto.getValue());
    projectRepository.save(project);

    if (dto.getThrowExceptionInside()) {
      throw new BadRequestException("ThrowExceptionInside");
    }
  }
}
