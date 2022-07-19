package me.lozm.app.contract.service;

import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import me.lozm.app.contract.vo.ContractSellVo;

public interface ContractService {

    ContractMintVo.Response mintToken(ContractMintVo.Request requestVo);

    ContractListVo.Response getTokens(ContractListVo.Request requestVo);

    ContractSellVo.Response sellToken(ContractSellVo.Request requestVo);

}
