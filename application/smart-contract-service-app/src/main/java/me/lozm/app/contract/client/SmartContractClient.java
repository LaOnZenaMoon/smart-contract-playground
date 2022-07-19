package me.lozm.app.contract.client;

import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

public interface SmartContractClient {

    TransactionReceipt getTransactionReceipt(String transactionHash);

    EthCall callViewFunction(String contractAddress, Credentials senderCredentials, Function web3jFunction);

    EthSendTransaction callTransactionFunction(String contractAddress, Credentials senderCredentials, Function web3jFunction);

    EthSendTransaction callTransactionFunction(String contractAddress, Credentials senderCredentials, BigInteger messageValue, Function web3jFunction);

}
