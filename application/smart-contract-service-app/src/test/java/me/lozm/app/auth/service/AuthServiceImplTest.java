package me.lozm.app.auth.service;

import me.lozm.app.auth.vo.AuthSignInVo;
import me.lozm.app.auth.vo.AuthSignUpVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("local")
@SpringBootTest
class AuthServiceImplTest {

    @Autowired
    private AuthService authService;


    @DisplayName("사용자 회원가입 및 로그인 성공")
    @Test
    void signUpAndSignIn_success() {
        // Given
        final String loginId = "laonzenamoon";
        final String password = "asdfasdf1234";

        // When
        AuthSignUpVo.Request signUpRequestVo = new AuthSignUpVo.Request(loginId, password);
        AuthSignUpVo.Response signUpResponseVo = authService.signUp(signUpRequestVo);

        AuthSignInVo.Request signInRequestVo = new AuthSignInVo.Request(loginId, password);
        AuthSignInVo.Response signInResponseVo = authService.signIn(signInRequestVo);

        // Then
        assertEquals(signUpResponseVo.getWalletAddress(), signInResponseVo.getWalletAddress());
    }

}