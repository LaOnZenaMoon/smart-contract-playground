package me.lozm.app.contract.client;

import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface SmartContractClient {

    TransactionReceipt getTransactionReceipt(String transactionHash);

    EthCall callViewFunction(String contractAddress, Credentials senderCredentials, Function web3jFunction);

    EthSendTransaction callTransactionFunction(String contractAddress, Credentials senderCredentials, Function web3jFunction);

    EthSendTransaction callTransactionFunction(String contractAddress, Credentials senderCredentials, BigInteger messageValue, Function web3jFunction);

    <T extends Web3j, R> R callFunction(Web3jWrapperFunction<T, R> trFunction);

    void sendBalance(String userAddress, Convert.Unit unit, BigDecimal amount);
}
