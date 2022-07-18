package me.lozm.app.contract.service;

import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

public interface ContractService {

    ContractMintVo.Response mintToken(ContractMintVo.Request requestVo);

    ContractListVo.Response getTokens(ContractListVo.Request requestVo);

    EthSendTransaction setSaleLozmToken(String privateKey);

    TransactionReceipt getTransactionReceipt(String transactionHash);

}
