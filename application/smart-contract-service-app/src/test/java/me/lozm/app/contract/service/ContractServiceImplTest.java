package me.lozm.app.contract.service;

import lombok.extern.slf4j.Slf4j;
import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import me.lozm.global.config.SmartContractConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

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
    private SmartContractConfig smartContractConfig;


    // @Disabled
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

    // @Disabled
    @DisplayName("EOA token 조회 성공")
    @Test
    void getTokens_success() throws IOException {
        // Given
        final String systemPrivateKey = smartContractConfig.getEoa().getSystemPrivateKey();
        ClassPathResource sampleClassPathResource = new ClassPathResource("ipfs/sample.jpg");

        // When
        contractService.mintToken(new ContractMintVo.Request(systemPrivateKey, sampleClassPathResource.getFile()));
        ContractListVo.Response listResponseVo = contractService.getTokens(new ContractListVo.Request(systemPrivateKey));

        // Then
        assertTrue(isNotEmpty(listResponseVo));
        assertFalse(listResponseVo.getTokenList().isEmpty());
    }

}
