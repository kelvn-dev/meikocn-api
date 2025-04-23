package com.meikocn.api.service.provider;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.PageFilter;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.json.mgmt.permissions.Permission;
import com.auth0.json.mgmt.permissions.PermissionsPage;
import com.auth0.json.mgmt.resourceserver.ResourceServer;
import com.auth0.json.mgmt.resourceserver.Scope;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;
import com.auth0.net.Response;
import com.auth0.net.TokenRequest;
import com.meikocn.api.config.Auth0ClientConfig;
import com.meikocn.api.config.Auth0Config;
import com.meikocn.api.utils.RandomUtils;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class Auth0Service {

  private Auth0Config auth0Config;
  private Auth0ClientConfig auth0ClientConfig;
  private ManagementAPI managementAPI;
  private AuthAPI authAPI;

  public Auth0Service(Auth0Config auth0Config, Auth0ClientConfig auth0ClientConfig) {
    this.auth0Config = auth0Config;
    this.auth0ClientConfig = auth0ClientConfig;
    this.managementAPI = this.auth0ClientConfig.getManagementAPI();
    this.authAPI = this.auth0ClientConfig.getAuthAPI();
  }

  private <T> T executeRequest(Request<T> request) {
    try {
      Response<T> response = request.execute();
      return response.getBody();
    } catch (APIException e) {
      throw new RuntimeException(e.getDescription());
    } catch (Auth0Exception e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  public User getUserById(String userId) {
    UserFilter userFilter = new UserFilter();
    userFilter.withConnection(auth0Config.getDbConnection());
    Request<User> request = managementAPI.users().get(userId, userFilter);
    return executeRequest(request);
  }

  /**
   * Todo: implement sending user invitation instead of creation
   *
   * @param email
   * @param permissions
   * @return
   */
  public User createUser(String email, List<String> permissions) {
    User user = new User(auth0Config.getDbConnection());
    user.setEmail(email);
    user.setEmailVerified(true);
    user.setPassword(RandomUtils.generateUrlSafeRandomString(16).toCharArray());
    Request<User> request = managementAPI.users().create(user);
    user = executeRequest(request);

    List<Permission> auth0Permissions =
        permissions.stream()
            .map(
                s -> {
                  Permission permission = new Permission();
                  permission.setName(s);
                  permission.setResourceServerId(auth0Config.getApiIdentifier());
                  return permission;
                })
            .toList();
    Request<Void> permissionRequest =
        managementAPI.users().addPermissions(user.getId(), auth0Permissions);
    executeRequest(permissionRequest);
    return user;
  }

  @Cacheable(value = "userPermissionCache", key = "#userId")
  public List<Permission> getPermissionsByUserId(String userId) {
    Request<PermissionsPage> request =
        managementAPI.users().listPermissions(userId, new PageFilter());
    PermissionsPage permissionsPage = executeRequest(request);
    return permissionsPage.getItems();
  }

  @CacheEvict(cacheNames = "userPermissionCache", key = "#userId")
  public void updatePermissionsByUserId(String userId, List<String> toAdd, List<String> toRemove) {
    List<Permission> permissionsToAdd =
        toAdd.stream()
            .map(
                p -> {
                  Permission permission = new Permission();
                  permission.setName(p);
                  permission.setResourceServerId(auth0Config.getApiIdentifier());
                  return permission;
                })
            .toList();

    List<Permission> permissionsToRemove =
        toRemove.stream()
            .map(
                p -> {
                  Permission permission = new Permission();
                  permission.setName(p);
                  permission.setResourceServerId(auth0Config.getApiIdentifier());
                  return permission;
                })
            .toList();
    Request<Void> addRequest = managementAPI.users().addPermissions(userId, permissionsToAdd);
    Request<Void> removeRequest =
        managementAPI.users().removePermissions(userId, permissionsToRemove);
    executeRequest(addRequest);
    executeRequest(removeRequest);
  }

  @CacheEvict(cacheNames = "userPermissionCache", key = "#userId")
  public void deleteUser(String userId) {
    Request<Void> request = managementAPI.users().delete(userId);
    executeRequest(request);
  }

  @Cacheable(value = "auth0PermissionCache")
  public List<Scope> getPermissions() {
    Request<ResourceServer> request =
        managementAPI.resourceServers().get(auth0Config.getApiIdentifier());
    ResourceServer resourceServer = executeRequest(request);
    return resourceServer.getScopes();
  }

  public User updatePassword(String userId, String password) {
    User user = new User(auth0Config.getDbConnection());
    user.setPassword(password.toCharArray());
    Request<User> request = managementAPI.users().update(userId, user);
    return executeRequest(request);
  }

  public TokenHolder login(String email, String password) {
    TokenRequest tokenRequest = authAPI.login(email, password.toCharArray());
    return executeRequest(tokenRequest);
  }
}
