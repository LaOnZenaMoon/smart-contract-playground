package me.lozm.app.user.service;

import me.lozm.app.user.vo.UserSignInVo;
import me.lozm.app.user.vo.UserSignUpVo;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;

import java.io.IOException;

public interface UserService {

    UserSignUpVo.Response signUp(UserSignUpVo.Request requestVo);

    UserSignInVo.Response signIn(UserSignInVo.Request requestVo);

    Credentials getCredentialsUsingWallet(String loginId, String password);

    Credentials getCredentialsFromWallet(String password, String walletFileName) throws IOException, CipherException;
}
