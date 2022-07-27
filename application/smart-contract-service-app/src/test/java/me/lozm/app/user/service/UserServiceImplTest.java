package me.lozm.app.user.service;

import me.lozm.app.user.vo.UserSignInVo;
import me.lozm.app.user.vo.UserSignUpVo;
import me.lozm.global.utils.SwaggerUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("local")
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;


    @DisplayName("사용자 회원가입 및 로그인 성공")
    @Test
    void signUpAndSignIn_success() {
        // Given
        final String loginId = SwaggerUtils.LOGIN_ID_EXAMPLE;
        final String password = SwaggerUtils.PASSWORD_EXAMPLE;

        // When
        UserSignUpVo.Request signUpRequestVo = new UserSignUpVo.Request(loginId, password);
        UserSignUpVo.Response signUpResponseVo = userService.signUp(signUpRequestVo);

        UserSignInVo.Request signInRequestVo = new UserSignInVo.Request(loginId, password);
        UserSignInVo.Response signInResponseVo = userService.signIn(signInRequestVo);

        // Then
        assertEquals(signUpResponseVo.getWalletAddress(), signInResponseVo.getWalletAddress());
    }

}