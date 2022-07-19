package me.lozm.app.contract.service;

import lombok.extern.slf4j.Slf4j;
import me.lozm.app.contract.client.SmartContractClient;
import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import me.lozm.app.contract.vo.ContractPurchaseVo;
import me.lozm.app.contract.vo.ContractSellVo;
import me.lozm.global.config.SmartContractConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest
class ContractServiceImplTest {

    @Autowired
    private ContractService contractService;

    @Autowired
    private SmartContractClient smartContractClient;

    @Autowired
    private SmartContractConfig smartContractConfig;


    @Disabled
    @DisplayName("mint token 성공")
    @ParameterizedTest(name = "{index}. {displayName} 입력값={0}")
    @ValueSource(strings = {"hello.txt", "sample.jpg"})
    void mintToken_success(final String fileName) throws IOException {
        // Given
        final String systemPrivateKey = smartContractConfig.getEoa().getSystemPrivateKey();
        ClassPathResource sampleClassPathResource = new ClassPathResource("ipfs/" + fileName);
        ContractMintVo.Request requestVo = new ContractMintVo.Request(systemPrivateKey, sampleClassPathResource.getFile());

        // When
        ContractMintVo.Response responseVo = contractService.mintToken(requestVo);

        // Then
        assertTrue(isNotBlank(responseVo.getTokenUrl()));
    }

    @Disabled
    @DisplayName("token 목록 조회 성공")
    @Test
    void getTokens_success() throws IOException {
        // Given
        final String systemPrivateKey = smartContractConfig.getEoa().getSystemPrivateKey();
        ClassPathResource sampleClassPathResource = new ClassPathResource("ipfs/sample.jpg");

        // When
        log.info("1. mint token");
        contractService.mintToken(new ContractMintVo.Request(systemPrivateKey, sampleClassPathResource.getFile()));

        log.info("2. token 목록 조회");
        ContractListVo.Response listResponseVo = contractService.getTokens(new ContractListVo.Request(systemPrivateKey));

        // Then
        assertTrue(isNotEmpty(listResponseVo));
        assertFalse(listResponseVo.getTokenList().isEmpty());
    }

    @Disabled
    @DisplayName("token 판매 등록 성공")
    @Test
    void sellToken_success() throws IOException {
        // Given
        final String systemPrivateKey = smartContractConfig.getEoa().getSystemPrivateKey();

        ClassPathResource sampleClassPathResource = new ClassPathResource("ipfs/sample.jpg");
        final String tokenPrice = "10";

        // When
        log.info("1. mint token");
        contractService.mintToken(new ContractMintVo.Request(systemPrivateKey, sampleClassPathResource.getFile()));

        log.info("2. token 목록 조회");
        ContractListVo.Response listResponseVo = contractService.getTokens(new ContractListVo.Request(systemPrivateKey));
        List<ContractListVo.Detail> tokenList = listResponseVo.getTokenList();
        ContractListVo.Detail mintTokenDetail = tokenList.get(tokenList.size() - 1);

        log.info("3. token 판매 등록");
        ContractSellVo.Response sellResponseVo = contractService.sellToken(new ContractSellVo.Request(systemPrivateKey, mintTokenDetail.getTokenId().toString(), tokenPrice));

        log.info("4. token 판매 등록 트랜잭션 조회");
        TransactionReceipt transactionReceipt = smartContractClient.getTransactionReceipt(sellResponseVo.getTransactionHash());
        log.info(transactionReceipt.toString());

        // Then
        assertTrue(isNotEmpty(listResponseVo));
        assertFalse(listResponseVo.getTokenList().isEmpty());
        assertTrue(isNotEmpty(sellResponseVo));
        assertTrue(isNotBlank(sellResponseVo.getTransactionHash()));
        assertTrue(transactionReceipt.isStatusOK());
    }

    @Disabled
    @DisplayName("token 구매 성공")
    @Test
    void purchaseToken_success() throws IOException {
        // Given
        final String systemPrivateKey = smartContractConfig.getEoa().getSystemPrivateKey();
        final String samplePrivateKey = smartContractConfig.getEoa().getSamplePrivateKey();

        ClassPathResource sampleClassPathResource = new ClassPathResource("ipfs/sample.jpg");
        final String tokenPrice = "10";

        // When
        log.info("1. mint token");
        contractService.mintToken(new ContractMintVo.Request(systemPrivateKey, sampleClassPathResource.getFile()));

        log.info("2. token 판매자의 token 목록 조회");
        ContractListVo.Response listResponseVo1 = contractService.getTokens(new ContractListVo.Request(systemPrivateKey));
        log.info(listResponseVo1.toString());
        List<ContractListVo.Detail> tokenList1 = listResponseVo1.getTokenList();
        ContractListVo.Detail mintTokenDetail = tokenList1.get(tokenList1.size() - 1);
        final String tokenId = mintTokenDetail.getTokenId().toString();

        log.info("3. token 판매 등록");
        ContractSellVo.Response sellResponseVo = contractService.sellToken(new ContractSellVo.Request(systemPrivateKey, tokenId, tokenPrice));

        log.info("4. token 판매 등록 트랜잭션 조회");
        TransactionReceipt sellTransactionReceipt = smartContractClient.getTransactionReceipt(sellResponseVo.getTransactionHash());
        log.info(sellTransactionReceipt.toString());

        log.info("5. token 구매");
        ContractPurchaseVo.Response purchaseResponseVo = contractService.purchaseToken(new ContractPurchaseVo.Request(samplePrivateKey, tokenId, tokenPrice));

        log.info("6. token 구매 트랜잭션 조회");
        TransactionReceipt purchaseTransactionReceipt = smartContractClient.getTransactionReceipt(sellResponseVo.getTransactionHash());
        log.info(purchaseTransactionReceipt.toString());

        log.info("7. token 구매자의 token 목록 조회");
        ContractListVo.Response listResponseVo2 = contractService.getTokens(new ContractListVo.Request(samplePrivateKey));
        log.info(listResponseVo2.toString());

        // Then
        assertTrue(isNotEmpty(listResponseVo1));
        assertFalse(listResponseVo1.getTokenList().isEmpty());

        assertTrue(isNotEmpty(sellResponseVo));
        assertTrue(isNotBlank(sellResponseVo.getTransactionHash()));

        assertTrue(sellTransactionReceipt.isStatusOK());

        assertTrue(isNotEmpty(purchaseResponseVo));
        assertTrue(isNotBlank(purchaseResponseVo.getTransactionHash()));

        assertTrue(purchaseTransactionReceipt.isStatusOK());

        assertTrue(isNotEmpty(listResponseVo2));
        assertFalse(listResponseVo2.getTokenList().isEmpty());
    }

}
