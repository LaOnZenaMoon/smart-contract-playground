package me.lozm.app.user.service;

import me.lozm.app.user.vo.UserSignInVo;
import me.lozm.app.user.vo.UserSignUpVo;

public interface UserService {

    UserSignUpVo.Response signUp(UserSignUpVo.Request requestVo);

    UserSignInVo.Response signIn(UserSignInVo.Request requestVo);

}
