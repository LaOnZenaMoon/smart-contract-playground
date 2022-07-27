package me.lozm.app.user.service;

import me.lozm.app.user.vo.UserSignInVo;
import me.lozm.app.user.vo.UserSignUpVo;
import org.web3j.crypto.Credentials;

public interface UserService {

    UserSignUpVo.Response signUp(UserSignUpVo.Request requestVo);

    UserSignInVo.Response signIn(UserSignInVo.Request requestVo);

    Credentials getCredentialsUsingWallet(String loginId, String password);

}
