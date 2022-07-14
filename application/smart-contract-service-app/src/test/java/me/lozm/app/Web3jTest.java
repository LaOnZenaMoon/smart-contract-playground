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
        final String eoaPrivateKey = "4334f409334858cae7cb2413c393f3c6435324411c8e0cdf9f987f209ccb654d";
        Credentials callerCredentials = Credentials.create(eoaPrivateKey);

        Web3j web3j = Web3j.build(new HttpService());
        Request<?, EthGetTransactionCount> ethGetTransactionCountRequest = web3j.ethGetTransactionCount(callerCredentials.getAddress(), DefaultBlockParameterName.LATEST);

        final BigInteger gas = new BigInteger("1537000");
        final String mintTokenContractAddress = "0xD1d9fe55f1f3376C2E34e0AEB599521F164Ef8D6";
        final String saleTokenContractAddress = "0x6D6AB42d3834172aeB81973cC650107f5A625c89";

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
                callerCredentials.getAddress(), // from
                ethGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                mintTokenContractAddress, // to
                FunctionEncoder.encode(mintTokenFunction) // data
        );
        EthSendTransaction mintTokenTransactionResponse = web3j.ethSendTransaction(mintTokenFunctionCallTransaction).sendAsync().get();
        String result = mintTokenTransactionResponse.getResult();
        List<Type> outputList = FunctionReturnDecoder.decode(result, mintTokenFunction.getOutputParameters());

        //TODO mintToken function 호출에 대한 return 값 받기
        // 1. return tokenId 받아서 테스트 코드 진행시키기

        log.info("2. set approval for all");
        Transaction setApprovalForAllFunctionCallTransaction = Transaction.createFunctionCallTransaction(
                callerCredentials.getAddress(), // from
                ethGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                mintTokenContractAddress, // to
                FunctionEncoder.encode(new Function(
                        "setApprovalForAll",
                        List.of(new Address(saleTokenContractAddress), new Bool(true)),
                        List.of(new TypeReference<Type>() {})
                ) // data
        ));
        EthSendTransaction setApprovalForAllTransactionResponse = web3j.ethSendTransaction(setApprovalForAllFunctionCallTransaction).sendAsync().get();

        log.info("3. is approved for all");
        Transaction isApprovedForAllFunctionCallTransaction = Transaction.createFunctionCallTransaction(
                callerCredentials.getAddress(), // from
                ethGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                mintTokenContractAddress, // to
                FunctionEncoder.encode(new Function(
                                "isApprovedForAll",
                                List.of(new Address(callerCredentials.getAddress()), new Address(saleTokenContractAddress)),
                                List.of(new TypeReference<Type>() {})
                        ) // data
                ));
        EthSendTransaction isApprovedForAllTransactionResponse = web3j.ethSendTransaction(isApprovedForAllFunctionCallTransaction).sendAsync().get();

        log.info("4. set for sale token");
        final Long tokenId = 1L;
        final Long tokenPrice = 10L;
        Transaction setForSaleTokenFunctionCallTransaction = Transaction.createFunctionCallTransaction(
                callerCredentials.getAddress(), // from
                ethGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                saleTokenContractAddress, // to
                FunctionEncoder.encode(new Function(
                                "setForSaleToken",
                                List.of(new Uint256(tokenId), new Uint256(tokenPrice)),
                                List.of(new TypeReference<Type>() {})
                        ) // data
                ));
        EthSendTransaction setForSaleTokenTransactionResponse = web3j.ethSendTransaction(setForSaleTokenFunctionCallTransaction).sendAsync().get();

        // Then
        assertFalse(mintTokenTransactionResponse.hasError());
        assertTrue(isNotBlank(mintTokenTransactionResponse.getResult()));

        assertFalse(setApprovalForAllTransactionResponse.hasError());
        assertTrue(isNotBlank(setApprovalForAllTransactionResponse.getResult()));

        assertFalse(isApprovedForAllTransactionResponse.hasError());
        assertTrue(isNotBlank(isApprovedForAllTransactionResponse.getResult()));

//        assertFalse(setForSaleTokenTransactionResponse.hasError());
//        assertTrue(isNotBlank(setForSaleTokenTransactionResponse.getResult()));
    }

}
