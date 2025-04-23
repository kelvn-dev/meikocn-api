package com.meikocn.api.controller.rest;

import com.meikocn.api.dto.rest.request.UserResetReqDto;
import com.meikocn.api.service.rest.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user-action")
@RequiredArgsConstructor
public class UserActionController {

  private final UserService userService;

  @PostMapping("/reset/{token}")
  public ResponseEntity<?> resetUser(
      @PathVariable String token, @Valid @RequestBody UserResetReqDto reqDTO) {
    userService.reset(token, reqDTO);
    return ResponseEntity.ok(null);
  }
}
