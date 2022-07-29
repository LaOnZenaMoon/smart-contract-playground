package me.lozm.app.user.service;

import me.lozm.app.user.vo.UserSignInVo;
import me.lozm.app.user.vo.UserSignUpVo;
import me.lozm.domain.user.entity.User;
import me.lozm.domain.user.repository.UserRepository;
import me.lozm.domain.user.service.UserHelperService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;

import java.io.IOException;
import java.util.List;

import static me.lozm.global.utils.SwaggerUtils.LOGIN_ID_EXAMPLE;
import static me.lozm.global.utils.SwaggerUtils.PASSWORD_EXAMPLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled("테스트 시, IPFS Daemon 및 local 블록체인 네트워크 (container) 필요")
@ActiveProfiles("local")
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserHelperService userHelperService;


    private static UserSignUpVo.Response userVo;

    @BeforeAll
    static void beforeAll(@Autowired UserService userService) {
        userVo = userService.signUp(new UserSignUpVo.Request(LOGIN_ID_EXAMPLE, PASSWORD_EXAMPLE));
    }

    @AfterAll
    static void afterAll(@Autowired UserRepository userRepository) {
        List<User> signUpUserList = userRepository.findAllByLoginIdIn(List.of(LOGIN_ID_EXAMPLE));
        userRepository.deleteAll(signUpUserList);
    }


    @DisplayName("사용자 로그인 성공")
    @Test
    void signIn_success() {
        // Given
        // When
        UserSignInVo.Request signInRequestVo = new UserSignInVo.Request(LOGIN_ID_EXAMPLE, PASSWORD_EXAMPLE);
        UserSignInVo.Response signInResponseVo = userService.signIn(signInRequestVo);

        // Then
        assertEquals(userVo.getWalletAddress(), signInResponseVo.getWalletAddress());
    }

    @DisplayName("올바른 사용자 비밀번호를 사용하여, wallet 파일 로드 성공")
    @Test
    void getCredentialsFromWalletWithRightPassword_success() throws CipherException, IOException {
        // Given
        // When
        User user = userHelperService.getUserByLoginId(LOGIN_ID_EXAMPLE);
        Credentials credentialsFromWallet = userService.getCredentialsFromWallet(PASSWORD_EXAMPLE, user.getWalletFile());

        // Then
        assertEquals(userVo.getWalletAddress(), credentialsFromWallet.getAddress());
        assertEquals(userVo.getPrivateKey(), credentialsFromWallet.getEcKeyPair().getPrivateKey().toString());
    }

    @DisplayName("잘못된 사용자 비밀번호를 사용하여, wallet 파일 로드 실패")
    @Test
    void getCredentialsFromWalletWithWrongPassword_fail() {
        // Given
        final String wrongPassword = PASSWORD_EXAMPLE + "wrong";

        // When
        User user = userHelperService.getUserByLoginId(LOGIN_ID_EXAMPLE);

        // Then
        assertThrows(CipherException.class,
                () -> userService.getCredentialsFromWallet(wrongPassword, user.getWalletFile()));
    }

}