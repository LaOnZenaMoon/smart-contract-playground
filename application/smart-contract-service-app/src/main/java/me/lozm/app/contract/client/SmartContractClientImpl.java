package me.lozm.app.contract.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lozm.global.config.SmartContractConfig;
import me.lozm.utils.exception.BadRequestException;
import me.lozm.utils.exception.CustomExceptionType;
import me.lozm.utils.exception.InternalServerException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractClientImpl implements SmartContractClient {

    private final SmartContractConfig smartContractConfig;


    @Override
    public TransactionReceipt getTransactionReceipt(String transactionHash) {
        Web3j web3j = createWeb3j();

        try {
            EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).sendAsync().get();
            Optional<TransactionReceipt> transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt();
            if (transactionReceipt.isEmpty()) {
                throw new IllegalArgumentException(format("잘못된 transaction 값입니다. transaction hash: %s", transactionHash));
            }

            return transactionReceipt.get();

        } catch (ExecutionException e) {
            log.error(e.getMessage());
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_SMART_CONTRACT, e);

        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_SMART_CONTRACT, e);

        } finally {
            web3j.shutdown();
        }
    }

    @Override
    public EthCall callViewFunction(String contractAddress, Credentials senderCredentials, Function web3jFunction) {
        validateSmartContractAddress(contractAddress);

        Web3j web3j = createWeb3j();

        try {
            Transaction functionCallTransaction = Transaction.createEthCallTransaction(
                    senderCredentials.getAddress(), // from
                    contractAddress, // to
                    FunctionEncoder.encode(web3jFunction) // data
            );

            EthCall ethCall = web3j.ethCall(functionCallTransaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            if (ethCall.hasError()) {
                throw new BadRequestException(CustomExceptionType.INVALID_REQUEST_PARAMETERS);
            }

            return ethCall;

        } catch (ExecutionException e) {
            log.error(e.getMessage());
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_SMART_CONTRACT, e);

        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_SMART_CONTRACT, e);

        } finally {
            web3j.shutdown();
        }
    }

    @Override
    public EthSendTransaction callTransactionFunction(String contractAddress, Credentials senderCredentials, Function web3jFunction) {
        validateSmartContractAddress(contractAddress);

        Web3j web3j = createWeb3j();

        try {
            Transaction functionCallTransaction = Transaction.createFunctionCallTransaction(
                    senderCredentials.getAddress(), // from
                    web3j.ethGetTransactionCount(senderCredentials.getAddress(), DefaultBlockParameterName.LATEST).send().getTransactionCount(), // nonce
                    Transaction.DEFAULT_GAS, // gasPrice
                    new BigInteger(smartContractConfig.getGasLimit()), //gasLimit
                    contractAddress, // to
                    FunctionEncoder.encode(web3jFunction) // data
            );

            EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(functionCallTransaction).sendAsync().get();
            if (ethSendTransaction.hasError()) {
                throw new BadRequestException(CustomExceptionType.INVALID_REQUEST_PARAMETERS);
            }

            return ethSendTransaction;

        } catch (IOException | ExecutionException e) {
            log.error(e.getMessage());
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_SMART_CONTRACT, e);

        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_SMART_CONTRACT, e);

        } finally {
            web3j.shutdown();
        }
    }

    @NotNull
    private Web3j createWeb3j() {
        return Web3j.build(new HttpService());
    }

    private void validateSmartContractAddress(String contractAddress) {
        if (isBlank(contractAddress)) {
            throw new BadRequestException(CustomExceptionType.INVALID_REQUEST_PARAMETERS);
        }
    }

}
