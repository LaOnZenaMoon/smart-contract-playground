package me.lozm.domain.user.repository;

import me.lozm.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByWalletAddress(String walletAddress);

    List<User> findAllByLoginIdIn(List<String> loginIdList);
}
