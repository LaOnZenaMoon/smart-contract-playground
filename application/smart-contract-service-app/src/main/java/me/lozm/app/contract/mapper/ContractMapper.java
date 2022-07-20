package me.lozm.app.contract.mapper;

import me.lozm.app.contract.dto.ContractMintDto;
import me.lozm.app.contract.dto.ContractPurchaseDto;
import me.lozm.app.contract.dto.ContractSellDto;
import me.lozm.app.contract.vo.ContractMintVo;
import me.lozm.app.contract.vo.ContractPurchaseVo;
import me.lozm.app.contract.vo.ContractSellVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.io.File;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    @Mapping(source = "uploadFile", target = "uploadFile")
    @Mapping(source = "requestDto.privateKey", target = "privateKey")
    ContractMintVo.Request toMintVo(File uploadFile, ContractMintDto.Request requestDto);

    ContractMintDto.Response toMintDto(ContractMintVo.Response responseVo);

    @Mapping(source = "tokenId", target = "tokenId")
    @Mapping(source = "requestDto.privateKey", target = "privateKey")
    @Mapping(source = "requestDto.tokenPrice", target = "tokenPrice")
    ContractSellVo.Request toSellVo(String tokenId, ContractSellDto.Request requestDto);

    ContractSellDto.Response toSellDto(ContractSellVo.Response responseVo);

    @Mapping(source = "tokenId", target = "tokenId")
    @Mapping(source = "requestDto.privateKey", target = "privateKey")
    @Mapping(source = "requestDto.tokenPrice", target = "tokenPrice")
    ContractPurchaseVo.Request toPurchaseVo(String tokenId, ContractPurchaseDto.Request requestDto);

    ContractPurchaseDto.Response toPurchaseDto(ContractPurchaseVo.Response responseVo);
}
