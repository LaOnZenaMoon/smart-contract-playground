package me.lozm.app.contract.service;

import lombok.extern.slf4j.Slf4j;
import me.lozm.app.contract.code.TokenSearchType;
import me.lozm.app.contract.vo.ContractBuyVo;
import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import me.lozm.app.contract.vo.ContractSellVo;
import me.lozm.app.user.service.UserService;
import me.lozm.app.user.vo.UserSignUpVo;
import me.lozm.domain.user.entity.User;
import me.lozm.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

import static me.lozm.global.utils.SwaggerUtils.*;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("테스트 시, IPFS Daemon 및 local 블록체인 네트워크 (container) 필요")
@Slf4j
@ActiveProfiles("local")
@SpringBootTest
class ContractServiceImplTest {

    @Autowired
    private ContractService contractService;


    private static UserSignUpVo.Response sellerVo;
    private static UserSignUpVo.Response buyerVo;

    @BeforeAll
    static void beforeAll(@Autowired UserService userService) {
        sellerVo = userService.signUp(new UserSignUpVo.Request(SELLER_LOGIN_ID_EXAMPLE, PASSWORD_EXAMPLE));
        buyerVo = userService.signUp(new UserSignUpVo.Request(BUYER_LOGIN_ID_EXAMPLE, PASSWORD_EXAMPLE));
    }

    @AfterAll
    static void afterAll(@Autowired UserRepository userRepository) {
        List<User> signUpUserList = userRepository.findAllByLoginIdIn(List.of(SELLER_LOGIN_ID_EXAMPLE, BUYER_LOGIN_ID_EXAMPLE));
        userRepository.deleteAll(signUpUserList);
    }


    @DisplayName("mint token 성공")
    @ParameterizedTest(name = "{index}. {displayName} 입력값={0}")
    @ValueSource(strings = {"hello.txt", "sample.jpg"})
    void mintToken_success(final String fileName) throws IOException {
        // Given
        ClassPathResource sampleClassPathResource = new ClassPathResource("ipfs/" + fileName);
        ContractMintVo.Request requestVo = new ContractMintVo.Request(SELLER_LOGIN_ID_EXAMPLE, PASSWORD_EXAMPLE, sampleClassPathResource.getFile());

        // When
        ContractMintVo.Response responseVo = contractService.mintToken(requestVo);

        // Then
        assertTrue(isNotBlank(responseVo.getTokenUrl()));
    }

    @DisplayName("token 목록 조회 성공")
    @Test
    void getTokens_success() throws IOException {
        // Given
        ClassPathResource sampleClassPathResource = new ClassPathResource("ipfs/sample.jpg");

        // When
        log.info("1. mint token");
        contractService.mintToken(new ContractMintVo.Request(SELLER_LOGIN_ID_EXAMPLE, PASSWORD_EXAMPLE, sampleClassPathResource.getFile()));

        log.info("2. token 목록 조회");
        ContractListVo.Response listResponseVo = contractService.getTokens(
                new ContractListVo.Request(TokenSearchType.PRIVATE, sellerVo.getWalletAddress()));
        log.info(listResponseVo.toString());

        // Then
        assertTrue(isNotEmpty(listResponseVo));
        assertFalse(listResponseVo.getTokenList().isEmpty());
    }

    @DisplayName("token 판매 등록 성공")
    @Test
    void sellToken_success() throws IOException {
        // Given
        ClassPathResource sampleClassPathResource = new ClassPathResource("ipfs/sample.jpg");
        final String tokenPrice = "10";

        // When
        log.info("1. mint token");
        contractService.mintToken(new ContractMintVo.Request(SELLER_LOGIN_ID_EXAMPLE, PASSWORD_EXAMPLE, sampleClassPathResource.getFile()));

        log.info("2. token 목록 조회");
        ContractListVo.Response listResponseVo = contractService.getTokens(new ContractListVo.Request(TokenSearchType.PRIVATE, sellerVo.getWalletAddress()));
        List<ContractListVo.Detail> tokenList = listResponseVo.getTokenList();
        ContractListVo.Detail mintTokenDetail = tokenList.get(tokenList.size() - 1);

        log.info("3. token 판매 등록");
        ContractSellVo.Response sellResponseVo = contractService.sellToken(
                new ContractSellVo.Request(SELLER_LOGIN_ID_EXAMPLE, PASSWORD_EXAMPLE, mintTokenDetail.getTokenId().toString(), tokenPrice));

        // Then
        assertTrue(isNotEmpty(listResponseVo));
        assertFalse(listResponseVo.getTokenList().isEmpty());
        assertTrue(isNotEmpty(sellResponseVo));
        assertTrue(isNotBlank(sellResponseVo.getTransactionHash()));
    }

    @DisplayName("token 구매 성공")
    @Test
    void buyToken_success() throws IOException {
        // Given
        ClassPathResource sampleClassPathResource = new ClassPathResource("ipfs/sample.jpg");
        final String tokenPrice = "10";

        // When
        log.info("1. mint token");
        contractService.mintToken(new ContractMintVo.Request(SELLER_LOGIN_ID_EXAMPLE, PASSWORD_EXAMPLE, sampleClassPathResource.getFile()));

        log.info("2. token 판매자의 token 목록 조회");
        ContractListVo.Response listResponseVo1 = contractService.getTokens(
                new ContractListVo.Request(TokenSearchType.PRIVATE, sellerVo.getWalletAddress()));
        log.info(listResponseVo1.toString());
        List<ContractListVo.Detail> tokenList1 = listResponseVo1.getTokenList();
        ContractListVo.Detail mintTokenDetail = tokenList1.get(tokenList1.size() - 1);
        final String tokenId = mintTokenDetail.getTokenId().toString();

        log.info("3. token 판매 등록");
        ContractSellVo.Response sellResponseVo = contractService.sellToken(
                new ContractSellVo.Request(SELLER_LOGIN_ID_EXAMPLE, PASSWORD_EXAMPLE, tokenId, tokenPrice));

        log.info("4. token 구매");
        ContractBuyVo.Response buyResponseVo = contractService.buyToken(
                new ContractBuyVo.Request(BUYER_LOGIN_ID_EXAMPLE, PASSWORD_EXAMPLE, tokenId, tokenPrice));

        log.info("5. token 구매자의 token 목록 조회");
        ContractListVo.Response listResponseVo2 = contractService.getTokens(
                new ContractListVo.Request(TokenSearchType.PRIVATE, buyerVo.getWalletAddress()));
        log.info(listResponseVo2.toString());

        // Then
        assertTrue(isNotEmpty(listResponseVo1));
        assertFalse(listResponseVo1.getTokenList().isEmpty());

        assertTrue(isNotEmpty(sellResponseVo));
        assertTrue(isNotBlank(sellResponseVo.getTransactionHash()));

        assertTrue(isNotEmpty(buyResponseVo));
        assertTrue(isNotBlank(buyResponseVo.getTransactionHash()));

        assertTrue(isNotEmpty(listResponseVo2));
        assertFalse(listResponseVo2.getTokenList().isEmpty());
    }

}
