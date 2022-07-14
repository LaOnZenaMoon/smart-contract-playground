package me.lozm.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.jupiter.api.Assertions.*;

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

        Web3j web3j = Web3j.build(new HttpService("http://localhost:7545/"));
        Request<?, EthGetTransactionCount> ethGetTransactionCountRequest = web3j.ethGetTransactionCount(callerCredentials.getAddress(), DefaultBlockParameterName.LATEST);

        final BigInteger gas = new BigInteger("1537000");
        final String contractAddress = "0xd81C86a7B81B62C4bbD0d92Ef6da4BDF4b23C479"; // MintLozmToken.sol

        List<Type> inputParameters = Arrays.asList();
        List<TypeReference<?>> outputParameters = Arrays.asList(new TypeReference<Type>() {});

        final String functionName = "mintToken";
        Function function = new Function(
                functionName,
                inputParameters,
                outputParameters
        );
        final String encodedFunction = FunctionEncoder.encode(function);

        // When
        Transaction functionCallTransaction = Transaction.createFunctionCallTransaction(
                callerCredentials.getAddress(), // from
                ethGetTransactionCountRequest.send().getTransactionCount(), // nonce
                Transaction.DEFAULT_GAS, // gasPrice
                gas, //gasLimit
                contractAddress, // to
                encodedFunction // data
        );

        Request<?, EthSendTransaction> ethSendTransactionRequest = web3j.ethSendTransaction(functionCallTransaction);
        EthSendTransaction ethSendTransaction = ethSendTransactionRequest.sendAsync().get();

        // Then
        assertFalse(ethSendTransaction.hasError());
        assertTrue(isNotBlank(ethSendTransaction.getResult()));
    }

}
