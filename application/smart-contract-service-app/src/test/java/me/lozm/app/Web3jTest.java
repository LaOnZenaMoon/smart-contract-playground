package me.lozm.app;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
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

import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
class Web3jTest {

    @Disabled
    @DisplayName("스마트 컨트랙트 함수 호출 성공")
    @Test
    void callSmartContractFunction_success() throws IOException, ExecutionException, InterruptedException {
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
        final String mintTokenContractAddress = "0xF82de707918AAa0746c831598260b0Bca7676893";
        final String saleTokenContractAddress = "0x390B92dcF7031e1963EdfDc46751172eaa338E9B";

        // When
        log.info("1. mint token");
        Function mintTokenFunction = new Function("mintToken", List.of(new Utf8String("http://localhost:8080/ipfs/QmQzCQn4puG4qu8PVysxZmscmQ5vT1ZXpqo7f58Uh9QfyY")), List.of(new TypeReference<Type>() {
        }));
        Transaction mintTokenFunctionCallTransaction = Transaction.createFunctionCallTransaction(mintTokenCallerCredentials.getAddress(), // from
                mintTokenEthGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                mintTokenContractAddress, // to
                FunctionEncoder.encode(mintTokenFunction) // data
        );
        EthSendTransaction mintTokenTransactionResponse = mintTokenWeb3j.ethSendTransaction(mintTokenFunctionCallTransaction).sendAsync().get();

        log.info("2. set approval for all");
        Transaction setApprovalForAllFunctionCallTransaction = Transaction.createFunctionCallTransaction(mintTokenCallerCredentials.getAddress(), // from
                mintTokenEthGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                mintTokenContractAddress, // to
                FunctionEncoder.encode(new Function("setApprovalForAll", List.of(new Address(saleTokenContractAddress), new Bool(true)), List.of(new TypeReference<Type>() {
                        })) // data
                ));
        EthSendTransaction setApprovalForAllTransactionResponse = mintTokenWeb3j.ethSendTransaction(setApprovalForAllFunctionCallTransaction).sendAsync().get();

        log.info("3. is approved for all");
        Transaction isApprovedForAllFunctionCallTransaction = Transaction.createFunctionCallTransaction(mintTokenCallerCredentials.getAddress(), // from
                mintTokenEthGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                mintTokenContractAddress, // to
                FunctionEncoder.encode(new Function("isApprovedForAll", List.of(new Address(mintTokenCallerCredentials.getAddress()), new Address(saleTokenContractAddress)), List.of(new TypeReference<Type>() {
                        })) // data
                ));
        EthSendTransaction isApprovedForAllTransactionResponse = mintTokenWeb3j.ethSendTransaction(isApprovedForAllFunctionCallTransaction).sendAsync().get();

        log.info("4. set for sale token");
        final long tokenId = 6L;
        final long tokenPrice = 10L;
        Transaction setForSaleTokenFunctionCallTransaction = Transaction.createFunctionCallTransaction(mintTokenCallerCredentials.getAddress(), // from
                mintTokenEthGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                saleTokenContractAddress, // to
                FunctionEncoder.encode(new Function("setForSaleToken", List.of(new Uint256(tokenId), new Uint256(tokenPrice)), List.of(new TypeReference<Type>() {
                        })) // data
                ));
        EthSendTransaction setForSaleTokenTransactionResponse = mintTokenWeb3j.ethSendTransaction(setForSaleTokenFunctionCallTransaction).sendAsync().get();

        log.info("5. purchase token");
        Transaction purchaseTokenFunctionCallTransaction = Transaction.createFunctionCallTransaction(purchaseTokenCallerCredentials.getAddress(), // from
                purchaseTokenEthGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                saleTokenContractAddress, // to
                new BigInteger(String.valueOf(tokenPrice)), // msg.value
                FunctionEncoder.encode(new Function("purchaseToken", List.of(new Uint256(tokenId - 1)), List.of(new TypeReference<Type>() {
                        })) // data
                ));
        EthSendTransaction purchaseTokenTransactionResponse = mintTokenWeb3j.ethSendTransaction(purchaseTokenFunctionCallTransaction).sendAsync().get();

        // Then
        assertFalse(mintTokenTransactionResponse.hasError());

        assertFalse(setApprovalForAllTransactionResponse.hasError());

        assertFalse(isApprovedForAllTransactionResponse.hasError());

        assertFalse(setForSaleTokenTransactionResponse.hasError());

        assertFalse(purchaseTokenTransactionResponse.hasError());
    }

}
