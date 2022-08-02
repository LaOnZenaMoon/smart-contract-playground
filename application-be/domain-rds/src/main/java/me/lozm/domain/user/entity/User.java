package me.lozm.domain.user.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.lozm.global.model.entity.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)

@Entity
@Table(name = "USERS")
@Where(clause = "IS_USE = true")
@DynamicUpdate
@DynamicInsert
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "LOGIN_ID", nullable = false, length = 50, unique = true)
    private String loginId;

    @Column(name = "WALLET_ADDRESS", nullable = false, length = 300)
    private String walletAddress;

    @Column(name = "WALLET_FILE", nullable = false, length = 300)
    private String walletFile;

    @Column(name = "PASSWORD", nullable = false, unique = true)
    private String encryptedPassword;


    public static User create(String loginId, String encryptedPassword, String walletAddress, String walletFile) {
        User user = new User();
        user.isUse = true;
        user.loginId = loginId;
        user.encryptedPassword = encryptedPassword;
        user.walletAddress = walletAddress;
        user.walletFile = walletFile;
        return user;
    }

}
