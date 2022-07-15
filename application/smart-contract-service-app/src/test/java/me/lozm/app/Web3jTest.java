package me.lozm.app;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class Web3jTest {

    @DisplayName("EOA 계정 연결 성공")
    @Test
    void connectToEOA_success() {
        // Given
        final String eoaPrivateKey = "4334f409334858cae7cb2413c393f3c6435324411c8e0cdf9f987f209ccb654d";

        // When
        Credentials credentials = Credentials.create(eoaPrivateKey);

        // Then
        final String eoaAddress = credentials.getAddress();
        assertTrue(isNotBlank(eoaAddress));
    }

    @DisplayName("스마트 컨트랙트 함수 호출 성공")
    @Test
    void invokeSmartContractFunction_success() throws IOException, ExecutionException, InterruptedException {
        // Given
        final String mintTokenEoaPrivateKey = "4334f409334858cae7cb2413c393f3c6435324411c8e0cdf9f987f209ccb654d";
        Credentials mintTokenCallerCredentials = Credentials.create(mintTokenEoaPrivateKey);

        final String purchaseTokenEoaPrivateKey = "606ec4972e7222e3d05b08e28648fea50feed7d6f0c9549c7960c724907c1a8d";
        Credentials purchaseTokenCallerCredentials = Credentials.create(purchaseTokenEoaPrivateKey);

        Web3j mintTokenWeb3j = Web3j.build(new HttpService());
        Request<?, EthGetTransactionCount> mintTokenEthGetTransactionCountRequest = mintTokenWeb3j.ethGetTransactionCount(mintTokenCallerCredentials.getAddress(), DefaultBlockParameterName.LATEST);

        Web3j purchaseTokenWeb3j = Web3j.build(new HttpService());
        Request<?, EthGetTransactionCount> purchaseTokenEthGetTransactionCountRequest = purchaseTokenWeb3j.ethGetTransactionCount(purchaseTokenCallerCredentials.getAddress(), DefaultBlockParameterName.LATEST);

        final BigInteger gas = new BigInteger("1537000");
        final String mintTokenContractAddress = "0xfDCBf3Fb268FBe3ea60994261CE44E739B3DdF6f";
        final String saleTokenContractAddress = "0xe28F88E492d42596fF4374dDBC769c969aa1A5b5";

        // When
        log.info("1. mint token");
        Function mintTokenFunction = new Function(
                "mintToken",
                List.of(),
                List.of(new TypeReference<Type>() {
                    @Override
                    public java.lang.reflect.Type getType() {
                        return Uint256.class;
                    }
                }));
        Transaction mintTokenFunctionCallTransaction = Transaction.createFunctionCallTransaction(
                mintTokenCallerCredentials.getAddress(), // from
                mintTokenEthGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                mintTokenContractAddress, // to
                FunctionEncoder.encode(mintTokenFunction) // data
        );
        EthSendTransaction mintTokenTransactionResponse = mintTokenWeb3j.ethSendTransaction(mintTokenFunctionCallTransaction).sendAsync().get();
        //TODO mintToken function 호출에 대한 return 값 받기
        // 1. return tokenId 받아서 테스트 코드 진행시키기
        List<Type> outputList = FunctionReturnDecoder.decode(mintTokenTransactionResponse.getResult(), mintTokenFunction.getOutputParameters());
        log.info("mintToken function output: {}", outputList.toString());

        log.info("2. set approval for all");
        Transaction setApprovalForAllFunctionCallTransaction = Transaction.createFunctionCallTransaction(
                mintTokenCallerCredentials.getAddress(), // from
                mintTokenEthGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                mintTokenContractAddress, // to
                FunctionEncoder.encode(new Function(
                        "setApprovalForAll",
                        List.of(new Address(saleTokenContractAddress), new Bool(true)),
                        List.of(new TypeReference<Type>() {})
                ) // data
        ));
        EthSendTransaction setApprovalForAllTransactionResponse = mintTokenWeb3j.ethSendTransaction(setApprovalForAllFunctionCallTransaction).sendAsync().get();

        log.info("3. is approved for all");
        Transaction isApprovedForAllFunctionCallTransaction = Transaction.createFunctionCallTransaction(
                mintTokenCallerCredentials.getAddress(), // from
                mintTokenEthGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                mintTokenContractAddress, // to
                FunctionEncoder.encode(new Function(
                                "isApprovedForAll",
                                List.of(new Address(mintTokenCallerCredentials.getAddress()), new Address(saleTokenContractAddress)),
                                List.of(new TypeReference<Type>() {})
                        ) // data
                ));
        EthSendTransaction isApprovedForAllTransactionResponse = mintTokenWeb3j.ethSendTransaction(isApprovedForAllFunctionCallTransaction).sendAsync().get();

        log.info("4. set for sale token");
        final long tokenId = 6L;
        final long tokenPrice = 10L;
        Transaction setForSaleTokenFunctionCallTransaction = Transaction.createFunctionCallTransaction(
                mintTokenCallerCredentials.getAddress(), // from
                mintTokenEthGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                saleTokenContractAddress, // to
                FunctionEncoder.encode(new Function(
                                "setForSaleToken",
                                List.of(new Uint256(tokenId), new Uint256(tokenPrice)),
                                List.of(new TypeReference<Type>() {})
                        ) // data
                ));
        EthSendTransaction setForSaleTokenTransactionResponse = mintTokenWeb3j.ethSendTransaction(setForSaleTokenFunctionCallTransaction).sendAsync().get();

        log.info("5. purchase token");
        Transaction purchaseTokenFunctionCallTransaction = Transaction.createFunctionCallTransaction(
                purchaseTokenCallerCredentials.getAddress(), // from
                purchaseTokenEthGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                saleTokenContractAddress, // to
                new BigInteger(String.valueOf(tokenPrice)), // msg.value
                FunctionEncoder.encode(new Function(
                                "purchaseToken",
                                List.of(new Uint256(tokenId - 1)),
                                List.of(new TypeReference<Type>() {})
                        ) // data
                ));
        EthSendTransaction purchaseTokenTransactionResponse = mintTokenWeb3j.ethSendTransaction(purchaseTokenFunctionCallTransaction).sendAsync().get();

        // Then
        assertFalse(mintTokenTransactionResponse.hasError());
        assertTrue(isNotBlank(mintTokenTransactionResponse.getResult()));

        assertFalse(setApprovalForAllTransactionResponse.hasError());
        assertTrue(isNotBlank(setApprovalForAllTransactionResponse.getResult()));

        assertFalse(isApprovedForAllTransactionResponse.hasError());
        assertTrue(isNotBlank(isApprovedForAllTransactionResponse.getResult()));

        assertFalse(setForSaleTokenTransactionResponse.hasError());
        assertTrue(isNotBlank(setForSaleTokenTransactionResponse.getResult()));

        assertFalse(purchaseTokenTransactionResponse.hasError());
        assertTrue(isNotBlank(purchaseTokenTransactionResponse.getResult()));
    }

}
