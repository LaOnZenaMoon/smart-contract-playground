//package me.lozm.app;
//
//import me.lozm.global.config.SmartContractConfig;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.web3j.crypto.HSMPass;
//import org.web3j.ens.EnsResolver;
//import org.web3j.protocol.Web3j;
//import org.web3j.service.HSMRequestProcessor;
//import org.web3j.service.TxHSMSignService;
//import org.web3j.service.TxSignService;
//import org.web3j.tx.ChainIdLong;
//import org.web3j.tx.RawTransactionManager;
//
//@SpringBootTest
//class Web3jTest {
//
//    @Autowired
//    private SmartContractConfig smartContractConfig;
//
//    @DisplayName("개인키 없이 블록체인 트랜잭션 생성 테스트")
//    @Test
//    void test1() {
//        // Given
//        Web3j web3j = smartContractConfig.createWeb3jInstance();
//        HSMRequestProcessor hsmRequestProcessor = new EnsResolver(web3j);
//        HSMPass hsmPass = null;
//        TxSignService txSignService = new TxHSMSignService<>(hsmRequestProcessor, hsmPass);
//        RawTransactionManager rawTransactionManager = new RawTransactionManager(web3j, txSignService, ChainIdLong.NONE);
//
//        // When
//        //TODO 개인키 없이 스마트 컨트랙트 function 호출해보기
//
//        // Then
//
//    }
//
//}
