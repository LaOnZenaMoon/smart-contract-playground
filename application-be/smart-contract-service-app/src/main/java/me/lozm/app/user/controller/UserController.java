package me.lozm.app.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.lozm.app.user.dto.UserSignUpDto;
import me.lozm.app.user.mapper.UserMapper;
import me.lozm.app.user.service.UserService;
import me.lozm.app.user.vo.UserSignUpVo;
import me.lozm.global.model.CommonResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자")
@RequestMapping("users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;


    @Operation(summary = "사용자 회원가입")
    @PostMapping("sign-up")
    public ResponseEntity<CommonResponseDto<UserSignUpDto.SignUpResponse>> signUp(@RequestBody @Validated UserSignUpDto.SignUpRequest requestDto) {
        UserSignUpVo.Request requestVo = userMapper.toSignUpVo(requestDto);
        UserSignUpVo.Response responseVo = userService.signUp(requestVo);
        UserSignUpDto.SignUpResponse responseDto = userMapper.toSignUpDto(responseVo);
        return CommonResponseDto.created(responseDto);
    }

    //TODO 사용자 정보 복구 API 개발?

}
