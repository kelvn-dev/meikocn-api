package com.meikocn.api.service.rest;

import com.auth0.json.mgmt.permissions.Permission;
import com.meikocn.api.dto.rest.request.PasswordReqDto;
import com.meikocn.api.dto.rest.request.ProfileReqDto;
import com.meikocn.api.dto.rest.request.UserReqDto;
import com.meikocn.api.dto.rest.request.UserResetReqDto;
import com.meikocn.api.exception.BadRequestException;
import com.meikocn.api.exception.ConflictException;
import com.meikocn.api.exception.ForbiddenException;
import com.meikocn.api.exception.NotFoundException;
import com.meikocn.api.mapping.rest.UserMapper;
import com.meikocn.api.model.User;
import com.meikocn.api.repository.UserRepository;
import com.meikocn.api.service.provider.Auth0Service;
import com.meikocn.api.utils.HelperUtils;
import com.meikocn.api.utils.PredicateUtils;
import com.meikocn.api.utils.RandomUtils;
import com.meikocn.api.utils.SearchCriteria;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserMapper userMapper;
  private final UserRepository repository;
  private final Auth0Service auth0Service;

  public User getById(String id, boolean noException) {
    User user = repository.findById(id).orElse(null);
    if (Objects.isNull(user) && !noException) {
      throw new NotFoundException(User.class, "id", id);
    }
    return user;
  }

  public User getByEmail(String email, boolean noException) {
    User user = repository.findByEmail(email).orElse(null);
    if (Objects.isNull(user) && !noException) {
      throw new NotFoundException(User.class, "email", email);
    }
    return user;
  }

  public User getByToken(JwtAuthenticationToken token, boolean noException) {
    String userId = token.getToken().getSubject();
    return this.getById(userId, noException);
  }

  public User getByInviteToken(String inviteToken, boolean noException) {
    User user = repository.findByInviteToken(inviteToken).orElse(null);
    if (Objects.isNull(user) && !noException) {
      throw new NotFoundException(User.class, "inviteTokken", inviteToken);
    }
    return user;
  }

  public User getProfile(JwtAuthenticationToken jwtToken) {
    String userId = jwtToken.getToken().getSubject();
    return this.getById(userId, false);
  }

  /**
   * Use this service for normal signup flow
   *
   * @param userId
   * @return
   */
  public User createInternalUserFromAuth0User(String userId) {
    com.auth0.json.mgmt.users.User auth0User = auth0Service.getUserById(userId);
    User user = userMapper.auth02Model(auth0User);
    return repository.save(user);
  }

  public User updateByToken(JwtAuthenticationToken jwtToken, ProfileReqDto dto) {
    User user = this.getByToken(jwtToken, false);
    userMapper.updateModelFromDto(dto, user);
    return repository.save(user);
  }

  public void updatePassword(JwtAuthenticationToken jwtToken, PasswordReqDto dto) {
    String userId = jwtToken.getToken().getSubject();
    if (!userId.startsWith("auth0")) {
      throw new BadRequestException("Account is of type social");
    }
    User user = this.getByToken(jwtToken, false);
    try {
      auth0Service.login(user.getEmail(), dto.getOldPassword());
    } catch (Exception exception) {
      throw new ForbiddenException("Access denied");
    }
    auth0Service.updatePassword(userId, dto.getNewPassword());
  }

  @Transactional
  public void create(UserReqDto reqDto) {
    User user = this.getByEmail(reqDto.getEmail(), true);
    if (Objects.nonNull(user)) {
      throw new ConflictException(User.class, "email", user.getEmail());
    }
    com.auth0.json.mgmt.users.User auth0User =
        auth0Service.createUser(reqDto.getEmail(), reqDto.getPermissions());
    user = userMapper.auth02Model(auth0User);
    repository.save(user);
  }

  public User invite(UserReqDto reqDto) {
    User user = this.getByEmail(reqDto.getEmail(), true);
    if (Objects.isNull(user)) {
      com.auth0.json.mgmt.users.User auth0User =
          auth0Service.createUser(reqDto.getEmail(), reqDto.getPermissions());
      user = userMapper.auth02Model(auth0User);
    } else if (user.isEmailVerified()) {
      throw new ConflictException(User.class, "email", user.getEmail());
    }

    String token = RandomUtils.generateUrlSafeRandomString(32);
    user.setInviteToken(token);
    user.setEmailVerified(false);
    return repository.save(user);
  }

  public void reset(String token, UserResetReqDto reqDTO) {
    User user = this.getByInviteToken(token, false);
    user.setInviteToken(null);
    user.setEmailVerified(true);
    auth0Service.updatePassword(user.getId(), reqDTO.getPassword());
  }

  public void updateById(String id, UserReqDto reqDto) {
    this.getById(id, false);
    List<Permission> permissions = auth0Service.getPermissionsByUserId(id);
    List<String> currentPermissions = permissions.stream().map(Permission::getName).toList();
    List<String> requestedPermissions = reqDto.getPermissions();
    Set<String> requestedSet = new HashSet<>(requestedPermissions);
    Set<String> currentSet = new HashSet<>(currentPermissions);

    List<String> permissionToAdd =
        requestedSet.stream().filter(permission -> !currentSet.contains(permission)).toList();

    List<String> permissionToRemove =
        currentSet.stream().filter(permission -> !requestedSet.contains(permission)).toList();

    auth0Service.updatePermissionsByUserId(id, permissionToAdd, permissionToRemove);
  }

  public Page<User> getList(List<String> filter, Pageable pageable) {
    List<SearchCriteria> criteria = HelperUtils.formatSearchCriteria(filter);
    BooleanExpression expression = PredicateUtils.getBooleanExpression(criteria, User.class);
    return repository.findAll(expression, pageable);
  }

  @Transactional
  public void deleteById(String id) {
    User user = this.getById(id, false);
    repository.delete(user);
    auth0Service.deleteUser(id);
  }
}
