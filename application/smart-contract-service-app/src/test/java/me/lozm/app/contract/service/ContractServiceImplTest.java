package me.lozm.app.contract.service;

import lombok.extern.slf4j.Slf4j;
import me.lozm.app.contract.client.SmartContractClient;
import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;

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


    @Disabled
    @DisplayName("mint token 성공")
    @ParameterizedTest(name = "{index}. {displayName} 입력값={0}")
    @ValueSource(strings = {"hello.txt", "sample.jpg"})
    void mintToken_success(final String fileName) throws IOException {
        // Given
        final String senderSecretKey = "4334f409334858cae7cb2413c393f3c6435324411c8e0cdf9f987f209ccb654d";
        ClassPathResource classPathResource = new ClassPathResource("ipfs/" + fileName);
        ContractMintVo.Request requestVo = new ContractMintVo.Request(senderSecretKey, classPathResource.getFile());

        // When
        ContractMintVo.Response responseVo = contractService.mintToken(requestVo);

        // Then
        assertTrue(isNotBlank(responseVo.getTokenUrl()));
    }

    @Disabled
    @DisplayName("SaleLozmToken 스마트 컨트랙트 등록 및 트랜잭션 결과 조회 성공")
    @Test
    void setSaleLozmToken_getTransactionReceipt_success() {
        // Given
        final String senderSecretKey = "4334f409334858cae7cb2413c393f3c6435324411c8e0cdf9f987f209ccb654d";

        // When
        EthSendTransaction ethSendTransaction = contractService.setSaleLozmToken(senderSecretKey);
        String transactionHash = ethSendTransaction.getTransactionHash();
        TransactionReceipt transactionReceipt = smartContractClient.getTransactionReceipt(transactionHash);
        log.info(transactionReceipt.toString());

        // Then
        assertFalse(ethSendTransaction.hasError());
        assertTrue(transactionReceipt.isStatusOK());
    }

    @Disabled
    @DisplayName("EOA token 조회 성공")
    @Test
    void getTokens_success() {
        // Given
        final String senderSecretKey = "4334f409334858cae7cb2413c393f3c6435324411c8e0cdf9f987f209ccb654d";

        // When
        ContractListVo.Response responseVo = contractService.getTokens(new ContractListVo.Request(senderSecretKey));

        // Then
        assertTrue(isNotEmpty(responseVo));
    }

}
