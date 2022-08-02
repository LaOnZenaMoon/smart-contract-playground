package me.lozm.app.contract.mapper;

import me.lozm.app.contract.dto.ContractBuyDto;
import me.lozm.app.contract.dto.ContractListDto;
import me.lozm.app.contract.dto.ContractMintDto;
import me.lozm.app.contract.dto.ContractSellDto;
import me.lozm.app.contract.vo.ContractBuyVo;
import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import me.lozm.app.contract.vo.ContractSellVo;
import me.lozm.web3j.MintLozmToken;
import me.lozm.web3j.SaleLozmToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.io.File;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContractMapper {

    @Mapping(source = "uploadFile", target = "uploadFile")
    @Mapping(source = "requestDto.loginId", target = "loginId")
    @Mapping(source = "requestDto.password", target = "password")
    ContractMintVo.Request toMintVo(File uploadFile, ContractMintDto.MintRequest requestDto);

    ContractMintDto.MintResponse toMintDto(ContractMintVo.Response responseVo);

    @Mapping(source = "tokenId", target = "tokenId")
    @Mapping(source = "requestDto.loginId", target = "loginId")
    @Mapping(source = "requestDto.password", target = "password")
    ContractSellVo.Request toSellVo(String tokenId, ContractSellDto.SellRequest requestDto);

    ContractSellDto.SellResponse toSellDto(ContractSellVo.Response responseVo);

    @Mapping(source = "tokenId", target = "tokenId")
    @Mapping(source = "requestDto.loginId", target = "loginId")
    @Mapping(source = "requestDto.password", target = "password")
    ContractBuyVo.Request toBuyVo(String tokenId, ContractBuyDto.BuyRequest requestDto);

    ContractBuyDto.BuyResponse toBuyDto(ContractBuyVo.Response responseVo);

    ContractListDto.ListDetail toListDto(ContractListVo.Detail vo);

    ContractListVo.Detail toListDetailVo(MintLozmToken.TokenData tokenData);

    ContractListVo.Detail toListDetailVo(SaleLozmToken.TokenData tokenData);
}
