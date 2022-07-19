package me.lozm.app.contract.service;

import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;

public interface ContractService {

    ContractMintVo.Response mintToken(ContractMintVo.Request requestVo);

    ContractListVo.Response getTokens(ContractListVo.Request requestVo);

}
