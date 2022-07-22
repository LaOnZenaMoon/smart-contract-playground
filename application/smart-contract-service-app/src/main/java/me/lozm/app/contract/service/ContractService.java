package me.lozm.app.contract.service;

import me.lozm.app.contract.vo.ContractBuyVo;
import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import me.lozm.app.contract.vo.ContractSellVo;
import me.lozm.web3j.MintLozmToken;
import me.lozm.web3j.SaleLozmToken;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

public interface ContractService {

    ContractMintVo.Response mintToken(ContractMintVo.Request requestVo);

    ContractListVo.Response getTokens(ContractListVo.Request requestVo);

    ContractSellVo.Response sellToken(ContractSellVo.Request requestVo);

    ContractBuyVo.Response buyToken(ContractBuyVo.Request requestVo);

    MintLozmToken getMintLozmTokenInstance(Credentials senderCredentials, Web3j web3j);

    SaleLozmToken getSaleLozmTokenInstance(Credentials senderCredentials, Web3j web3j);

}
