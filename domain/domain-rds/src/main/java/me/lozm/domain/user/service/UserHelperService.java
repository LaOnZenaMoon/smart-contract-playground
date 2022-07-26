package me.lozm.domain.user.service;

import lombok.RequiredArgsConstructor;
import me.lozm.domain.user.entity.User;
import me.lozm.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserHelperService {

    private final UserRepository userRepository;


    public Optional<User> findUser(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    public User getUser(String loginId) {
        return findUser(loginId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("존재하지 않는 사용자입니다. 사용자 계정: [%s]", loginId)));
    }

}
