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
    ContractMintVo.Request toMintVo(File uploadFile, ContractMintDto.MintRequest requestDto);

    ContractMintDto.MintResponse toMintDto(ContractMintVo.Response responseVo);

    @Mapping(source = "tokenId", target = "tokenId")
    @Mapping(source = "requestDto.privateKey", target = "privateKey")
    @Mapping(source = "requestDto.tokenPrice", target = "tokenPrice")
    ContractSellVo.Request toSellVo(String tokenId, ContractSellDto.SellRequest requestDto);

    ContractSellDto.SellResponse toSellDto(ContractSellVo.Response responseVo);

    @Mapping(source = "tokenId", target = "tokenId")
    @Mapping(source = "requestDto.privateKey", target = "privateKey")
    @Mapping(source = "requestDto.tokenPrice", target = "tokenPrice")
    ContractPurchaseVo.Request toPurchaseVo(String tokenId, ContractPurchaseDto.PurchaseRequest requestDto);

    ContractPurchaseDto.PurchaseResponse toPurchaseDto(ContractPurchaseVo.Response responseVo);

    @Mapping(source = "privateKey", target = "privateKey")
    @Mapping(source = "uploadFile", target = "uploadFile")
    ContractMintVo.Request toMintVo(String privateKey, File uploadFile);
}
