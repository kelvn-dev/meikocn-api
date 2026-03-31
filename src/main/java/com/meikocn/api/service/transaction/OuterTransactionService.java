package com.meikocn.api.service.transaction;

import com.meikocn.api.exception.BadRequestException;
import com.meikocn.api.exception.NotFoundException;
import com.meikocn.api.model.Task;
import com.meikocn.api.model.User;
import com.meikocn.api.repository.ProjectRepository;
import com.meikocn.api.repository.TaskRepository;
import com.meikocn.api.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OuterTransactionService {
  private final UserRepository userRepository;
  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;
  private final InnerTransactionService innerTransactionService;

  @Transactional()
  public void updateUser(TransactionDto dto) throws BadRequestException {
    String userId = "auth0|680fe9b15e50be9b026f7e13";
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException(User.class, "id", userId));

    user.setNickname(dto.getValue());
    userRepository.save(user);

    if (dto.getThrowExceptionOutsideBefore()) {
      throw new BadRequestException("ThrowExceptionOutsideBefore");
    }

    //        innerTransactionService.updateProject(dto);
    try {
      innerTransactionService.updateProject(dto);
    } catch (BadRequestException e) {
      System.out.println(e.getMessage());
    }

    String taskId = "bfe1d97a-a1b7-46c4-b4ad-666e14d70bdd";
    Task task =
        taskRepository
            .findById(UUID.fromString(taskId))
            .orElseThrow(() -> new NotFoundException(Task.class, "id", taskId));
    task.setName(dto.getValue());
    taskRepository.save(task);

    if (dto.getThrowExceptionOutsideAfter()) {
      throw new BadRequestException("ThrowExceptionOutsideAfter");
    }
  }

  //    @Transactional(propagation = Propagation.REQUIRED)
  //    public void updateProject(TransactionDto dto) throws BadRequestException {
  //        String projectId = "097b29a3-7938-433b-a72a-0d56cb05c46f";
  //        Project project = projectRepository.findById(UUID.fromString(projectId)).orElseThrow(()
  // -> new NotFoundException(Project.class, "id", projectId));
  //
  //        project.setName(dto.getValue());
  //        projectRepository.save(project);
  //
  //        if (dto.getThrowExceptionInside()) {
  //            throw new BadRequestException("ThrowExceptionInside");
  //        }
  //    }
}
/** 097b29a3-7938-433b-a72a-0d56cb05c46f bfe1d97a-a1b7-46c4-b4ad-666e14d70bdd */
