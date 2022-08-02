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


    public Optional<User> findUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    public User getUserByLoginId(String loginId) {
        return findUserByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("존재하지 않는 사용자입니다. 로그인 ID: [%s]", loginId)));
    }

    public Optional<User> findUserByWalletAddress(String walletAddress) {
        return userRepository.findByWalletAddress(walletAddress);
    }

    public User getUserByWalletAddress(String walletAddress) {
        return findUserByWalletAddress(walletAddress)
                .orElseThrow(() -> new IllegalArgumentException(String.format("존재하지 않는 사용자입니다. 지갑 주소: [%s]", walletAddress)));
    }

}
