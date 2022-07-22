package me.lozm.global.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lozm.app.contract.client.SmartContractClient;
import me.lozm.app.contract.client.Web3jWrapperFunction;
import me.lozm.app.contract.service.ContractService;
import me.lozm.global.config.SmartContractConfig;
import me.lozm.utils.exception.CustomExceptionType;
import me.lozm.utils.exception.InternalServerException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractInitializer implements CommandLineRunner {

    private final Environment environment;
    private final SmartContractConfig smartContractConfig;
    private final SmartContractClient smartContractClient;
    private final ContractService contractService;


    @Override
    public void run(String... args) {
        boolean smartContractIsInitialized = validateInitializeValue(environment.getProperty("smart-contracts.initialize"));
        if (smartContractIsInitialized) {
            Credentials systemCredentials = Credentials.create(smartContractConfig.getEoa().getSystemPrivateKey());
            setApprovalForAll(systemCredentials);
            isApprovedForAll(systemCredentials);
            setSaleLozmToken(systemCredentials);
        }
    }

    private boolean validateInitializeValue(String initializeValue) {
        try {
            return Boolean.parseBoolean(initializeValue);
        } catch (Exception e) {
            log.error("smart-contracts.initialize 설정값이 잘못되었습니다. smart-contracts.initialize 설정값: {}", initializeValue);
            throw e;
        }
    }

    private void setApprovalForAll(Credentials systemCredentials) {
        Web3jWrapperFunction<Web3j, TransactionReceipt> function = web3j ->
                contractService.getMintLozmTokenInstance(systemCredentials, web3j)
                        .setApprovalForAll(smartContractConfig.getContractAddress().getSaleToken(), true)
                        .sendAsync()
                        .get();

        TransactionReceipt transactionReceipt = smartContractClient.callFunction(function);
        if (!transactionReceipt.isStatusOK()) {
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_SMART_CONTRACT);
        }
    }

    private void isApprovedForAll(Credentials systemCredentials) {
        Web3jWrapperFunction<Web3j, Boolean> function = web3j ->
                contractService.getMintLozmTokenInstance(systemCredentials, web3j)
                        .isApprovedForAll(systemCredentials.getAddress(), smartContractConfig.getContractAddress().getSaleToken())
                        .sendAsync()
                        .get();

        Boolean isSuccess = smartContractClient.callFunction(function);
        if (!isSuccess) {
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_SMART_CONTRACT);
        }
    }

    private void setSaleLozmToken(Credentials systemCredentials) {
        Web3jWrapperFunction<Web3j, TransactionReceipt> function = web3j ->
                contractService.getMintLozmTokenInstance(systemCredentials, web3j)
                        .setSaleLozmToken(smartContractConfig.getContractAddress().getSaleToken())
                        .sendAsync()
                        .get();

        TransactionReceipt transactionReceipt = smartContractClient.callFunction(function);
        if (!transactionReceipt.isStatusOK()) {
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_SMART_CONTRACT);
        }
    }

}
