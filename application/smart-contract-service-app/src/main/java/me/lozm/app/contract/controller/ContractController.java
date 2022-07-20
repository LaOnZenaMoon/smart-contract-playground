package me.lozm.app.contract.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lozm.app.contract.dto.ContractListDto;
import me.lozm.app.contract.dto.ContractMintDto;
import me.lozm.app.contract.dto.ContractPurchaseDto;
import me.lozm.app.contract.dto.ContractSellDto;
import me.lozm.app.contract.mapper.ContractMapper;
import me.lozm.app.contract.service.ContractService;
import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import me.lozm.app.contract.vo.ContractPurchaseVo;
import me.lozm.app.contract.vo.ContractSellVo;
import me.lozm.global.config.IpfsConfig;
import me.lozm.global.model.CommonResponseDto;
import me.lozm.utils.exception.BadRequestException;
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

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequestMapping("tokens")
@RestController
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final ContractMapper contractMapper;
    private final IpfsConfig ipfsConfig;


    @PostMapping(value = "mint", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponseDto<ContractMintDto.Response>> mintToken(
            @RequestPart("request-dto") @Validated ContractMintDto.Request requestDto,
            @RequestPart("upload-file") MultipartFile multipartFile) {

        File file = uploadTempFile(multipartFile);
        ContractMintVo.Request requestVo = contractMapper.toMintVo(file, requestDto);
        ContractMintVo.Response responseVo = contractService.mintToken(requestVo);
        ContractMintDto.Response responseDto = contractMapper.toMintDto(responseVo);
        return CommonResponseDto.created(responseDto);
    }

    @GetMapping
    public ResponseEntity<CommonResponseDto<ContractListDto.Response>> getTokens(@RequestParam("privateKey") String privateKey) {
        ContractListVo.Response responseVo = contractService.getTokens(new ContractListVo.Request(privateKey));

        List<ContractListDto.Detail> tokenList = responseVo.getTokenList()
                .stream()
                .map(vo -> new ContractListDto.Detail(vo.getTokenId(), format(ipfsConfig.getPrefixUrl(), vo.getTokenUrl()), vo.getTokenPrice()))
                .collect(toList());
        return CommonResponseDto.ok(new ContractListDto.Response(tokenList));
    }

    @PostMapping("{tokenId}/sell")
    public ResponseEntity<CommonResponseDto<ContractSellDto.Response>> sellToken(@PathVariable("tokenId") String tokenId, @RequestBody @Validated ContractSellDto.Request requestDto) {
        ContractSellVo.Request requestVo = contractMapper.toSellVo(tokenId, requestDto);
        ContractSellVo.Response responseVo = contractService.sellToken(requestVo);
        ContractSellDto.Response responseDto = contractMapper.toSellDto(responseVo);
        return CommonResponseDto.created(responseDto);
    }

    @PostMapping("{tokenId}/purchase")
    public ResponseEntity<CommonResponseDto<ContractPurchaseDto.Response>> purchaseToken(@PathVariable("tokenId") String tokenId, @RequestBody @Validated ContractPurchaseDto.Request requestDto) {
        ContractPurchaseVo.Request requestVo = contractMapper.toPurchaseVo(tokenId, requestDto);
        ContractPurchaseVo.Response responseVo = contractService.purchaseToken(requestVo);
        ContractPurchaseDto.Response responseDto = contractMapper.toPurchaseDto(responseVo);
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
            throw new BadRequestException(CustomExceptionType.ALREADY_EXIST_UPLOAD_FILE);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR, e);
        }
    }

}
