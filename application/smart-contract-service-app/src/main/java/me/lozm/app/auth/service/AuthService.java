package me.lozm.app.auth.service;

import me.lozm.app.auth.vo.AuthSignInVo;
import me.lozm.app.auth.vo.AuthSignUpVo;

public interface AuthService {

    AuthSignUpVo.Response signUp(AuthSignUpVo.Request requestVo);

    AuthSignInVo.Response signIn(AuthSignInVo.Request requestVo);

}
