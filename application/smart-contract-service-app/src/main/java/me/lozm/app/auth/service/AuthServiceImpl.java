package me.lozm.app.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lozm.app.auth.vo.AuthSignInVo;
import me.lozm.app.auth.vo.AuthSignUpVo;
import me.lozm.domain.user.entity.User;
import me.lozm.domain.user.repository.UserRepository;
import me.lozm.domain.user.service.UserHelperService;
import me.lozm.global.config.SmartContractConfig;
import me.lozm.utils.exception.BadRequestException;
import me.lozm.utils.exception.CustomExceptionType;
import me.lozm.utils.exception.InternalServerException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SmartContractConfig smartContractConfig;
    private final UserRepository userRepository;
    private final UserHelperService userHelperService;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public AuthSignUpVo.Response signUp(AuthSignUpVo.Request requestVo) {
        try {
            final String loginId = requestVo.getLoginId();
            if (userHelperService.findUser(loginId).isPresent()) {
                throw new BadRequestException(CustomExceptionType.ALREADY_EXIST_LOGIN_ID);
            }

            Files.createDirectories(Paths.get(smartContractConfig.getWallet().getUploadPath())
                    .toAbsolutePath()
                    .normalize());

            File walletFile = new File(smartContractConfig.getWallet().getUploadPath());
            Bip39Wallet wallet = WalletUtils.generateBip39Wallet(requestVo.getPassword(), walletFile);
            Credentials credentialsFromWallet = getCredentialsFromWallet(requestVo.getPassword(), wallet.getFilename());

            final String encodedPassword = passwordEncoder.encode(requestVo.getPassword());
            userRepository.save(User.create(loginId, encodedPassword, wallet.getFilename()));

            return AuthSignUpVo.Response.builder()
                    .filename(wallet.getFilename())
                    .privateKey(credentialsFromWallet.getEcKeyPair().getPrivateKey().toString())
                    .mnemonic(wallet.getMnemonic())
                    .walletAddress(credentialsFromWallet.getAddress())
                    .build();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR);
        } catch (CipherException e) {
            log.error(e.getMessage());
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_WALLET);
        }
    }

    @Override
    public AuthSignInVo.Response signIn(AuthSignInVo.Request requestVo) {
        try {
            final String password = requestVo.getPassword();
            User user = userHelperService.getUser(requestVo.getLoginId());
            boolean matches = passwordEncoder.matches(password, user.getEncryptedPassword());
            if (!matches) {
                throw new BadRequestException(CustomExceptionType.INVALID_USER_PASSWORD);
            }

            Credentials credentials = getCredentialsFromWallet(password, user.getWallet());
            return new AuthSignInVo.Response(credentials.getAddress());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR);
        } catch (CipherException e) {
            log.error(e.getMessage());
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_WALLET);
        }
    }

    private Credentials getCredentialsFromWallet(String password, String walletFileName) throws IOException, CipherException {
        File walletFile = new File(smartContractConfig.getWallet().getUploadPath() + File.separator + walletFileName);
        return WalletUtils.loadCredentials(password, walletFile);
    }

}
