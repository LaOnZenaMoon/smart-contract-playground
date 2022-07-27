package me.lozm.app.contract.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lozm.app.contract.code.TokenSearchType;
import me.lozm.app.contract.dto.ContractBuyDto;
import me.lozm.app.contract.dto.ContractListDto;
import me.lozm.app.contract.dto.ContractMintDto;
import me.lozm.app.contract.dto.ContractSellDto;
import me.lozm.app.contract.mapper.ContractMapper;
import me.lozm.app.contract.service.ContractService;
import me.lozm.app.contract.vo.ContractBuyVo;
import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import me.lozm.app.contract.vo.ContractSellVo;
import me.lozm.global.config.IpfsConfig;
import me.lozm.global.model.CommonResponseDto;
import me.lozm.utils.exception.CustomExceptionType;
import me.lozm.utils.exception.InternalServerException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Tag(name = "토큰")
@RequestMapping("tokens")
@RestController
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final ContractMapper contractMapper;
    private final IpfsConfig ipfsConfig;


    @Operation(summary = "토큰 발급")
    @PostMapping(value = "mint", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommonResponseDto<ContractMintDto.MintResponse>> mintToken(
            @RequestPart("requestDto") @Validated ContractMintDto.MintRequest requestDto,
            @RequestPart("uploadFile") MultipartFile uploadFile) {

        File file = uploadTempFile(uploadFile);
        ContractMintVo.Request requestVo = contractMapper.toMintVo(file, requestDto);
        ContractMintVo.Response responseVo = contractService.mintToken(requestVo);
        ContractMintDto.MintResponse responseDto = contractMapper.toMintDto(responseVo);
        return CommonResponseDto.created(responseDto);
    }

    @Operation(summary = "토큰 목록 조회")
    @GetMapping
    public ResponseEntity<CommonResponseDto<ContractListDto.ListResponse>> getTokens(
            @RequestParam(value = "tokenSearchType", required = false) TokenSearchType tokenSearchType,
            @RequestParam(value = "walletAddress", required = false) String walletAddress) {

        ContractListVo.Response responseVo = contractService.getTokens(new ContractListVo.Request(tokenSearchType, walletAddress));

        List<ContractListDto.ListDetail> tokenList = responseVo.getTokenList()
                .stream()
                .map(contractMapper::toListDto)
                .collect(toList());
        return CommonResponseDto.ok(new ContractListDto.ListResponse(tokenList));
    }

    @Operation(summary = "토큰 판매")
    @PostMapping("{tokenId}/sell")
    public ResponseEntity<CommonResponseDto<ContractSellDto.SellResponse>> sellToken(@PathVariable("tokenId") String tokenId, @RequestBody @Validated ContractSellDto.SellRequest requestDto) {
        ContractSellVo.Request requestVo = contractMapper.toSellVo(tokenId, requestDto);
        ContractSellVo.Response responseVo = contractService.sellToken(requestVo);
        ContractSellDto.SellResponse responseDto = contractMapper.toSellDto(responseVo);
        return CommonResponseDto.created(responseDto);
    }

    @Operation(summary = "토큰 구매")
    @PostMapping("{tokenId}/buy")
    public ResponseEntity<CommonResponseDto<ContractBuyDto.BuyResponse>> buyToken(@PathVariable("tokenId") String tokenId, @RequestBody @Validated ContractBuyDto.BuyRequest requestDto) {
        ContractBuyVo.Request requestVo = contractMapper.toBuyVo(tokenId, requestDto);
        ContractBuyVo.Response responseVo = contractService.buyToken(requestVo);
        ContractBuyDto.BuyResponse responseDto = contractMapper.toBuyDto(responseVo);
        return CommonResponseDto.created(responseDto);
    }

    private File uploadTempFile(MultipartFile multipartFile) {
        final String filePath = ipfsConfig.getTempUploadPath() + File.separator + multipartFile.getOriginalFilename();

        try {
            Path createDirectories = Files.createDirectories(Paths.get(filePath)
                    .toAbsolutePath()
                    .normalize());
            Files.copy(multipartFile.getInputStream(), createDirectories, StandardCopyOption.REPLACE_EXISTING);
            return new File(filePath);

        } catch (FileAlreadyExistsException e) {
            log.info(e.getMessage());
            return new File(filePath);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR, e);
        }
    }

}
