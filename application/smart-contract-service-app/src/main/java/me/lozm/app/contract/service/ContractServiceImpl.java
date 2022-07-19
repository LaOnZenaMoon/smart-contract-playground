package me.lozm.app.contract.service;

import io.ipfs.multihash.Multihash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lozm.app.contract.client.IpfsClient;
import me.lozm.app.contract.client.SmartContractClient;
import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import me.lozm.app.contract.vo.ContractPurchaseVo;
import me.lozm.app.contract.vo.ContractSellVo;
import me.lozm.global.config.IpfsConfig;
import me.lozm.global.config.SmartContractConfig;
import me.lozm.utils.exception.BadRequestException;
import me.lozm.utils.exception.CustomExceptionType;
import me.lozm.utils.exception.InternalServerException;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final IpfsClient ipfsClient;
    private final SmartContractClient smartContractClient;
    private final SmartContractConfig smartContractConfig;
    private final IpfsConfig ipfsConfig;


    @PostConstruct
    public void initialize() {
        Credentials systemCredentials = Credentials.create(smartContractConfig.getEoa().getSystemPrivateKey());

        EthSendTransaction setApprovalForAllTransaction = setApprovalForAll(systemCredentials);
        validateInitialSetting(setApprovalForAllTransaction, "setApprovalForAll");

        EthSendTransaction approvedForAllTransaction = isApprovedForAll(systemCredentials);
        validateInitialSetting(approvedForAllTransaction, "isApprovedForAll");

        EthSendTransaction setSaleLozmTokenTransaction = setSaleLozmToken(systemCredentials);
        validateInitialSetting(setSaleLozmTokenTransaction, "setSaleLozmToken");
    }


    @Override
    public ContractMintVo.Response mintToken(ContractMintVo.Request requestVo) {
        Multihash multihash = ipfsClient.add(requestVo.getFile());

        smartContractClient.callTransactionFunction(
                smartContractConfig.getContractAddress().getMintToken(),
                Credentials.create(requestVo.getPrivateKey()),
                new Function(
                        "mintToken",
                        List.of(new Utf8String(multihash.toString())),
                        List.of(new TypeReference<Type>() {
                        })
                )
        );

        return new ContractMintVo.Response(format(ipfsConfig.getPrefixUrl(), multihash));
    }

    @Override
    public ContractListVo.Response getTokens(ContractListVo.Request requestVo) {
        List<ContractListVo.Detail> resultList = getTokens(Credentials.create(requestVo.getPrivateKey()));
        return new ContractListVo.Response(resultList);
    }

    @Override
    public ContractSellVo.Response sellToken(ContractSellVo.Request requestVo) {
        EthSendTransaction setForSaleTokenResponse = smartContractClient.callTransactionFunction(
                smartContractConfig.getContractAddress().getSaleToken(),
                Credentials.create(requestVo.getPrivateKey()),
                new Function(
                        "sellToken",
                        List.of(
                                new Uint256(requestVo.getTokenId()),
                                new Uint256(requestVo.getTokenPrice())
                        ),
                        List.of(new TypeReference<Type>() {
                        })
                )
        );
        return new ContractSellVo.Response(setForSaleTokenResponse.getTransactionHash());
    }

    @Override
    public ContractPurchaseVo.Response purchaseToken(ContractPurchaseVo.Request requestVo) {
        EthSendTransaction purchaseTokenResponse = smartContractClient.callTransactionFunction(
                smartContractConfig.getContractAddress().getSaleToken(),
                Credentials.create(requestVo.getPrivateKey()),
                requestVo.getTokenPrice(),
                new Function(
                        "purchaseToken",
                        List.of(
                                new Uint256(requestVo.getTokenId())
                        ),
                        List.of(new TypeReference<Type>() {
                        })
                )
        );
        return new ContractPurchaseVo.Response(purchaseTokenResponse.getTransactionHash());
    }

    private EthSendTransaction setApprovalForAll(Credentials systemCredentials) {
        return smartContractClient.callTransactionFunction(
                smartContractConfig.getContractAddress().getMintToken(),
                systemCredentials,
                new Function(
                        "setApprovalForAll",
                        List.of(
                                new Address(smartContractConfig.getContractAddress().getSaleToken()),
                                new Bool(true)
                        ),
                        List.of(new TypeReference<Type>() {})
                )
        );
    }

    private EthSendTransaction isApprovedForAll(Credentials systemCredentials) {
        return smartContractClient.callTransactionFunction(
                smartContractConfig.getContractAddress().getMintToken(),
                systemCredentials,
                new Function(
                        "isApprovedForAll",
                        List.of(
                                new Address(systemCredentials.getAddress()),
                                new Address(smartContractConfig.getContractAddress().getSaleToken())
                        ),
                        List.of(new TypeReference<Type>() {})
                )
        );
    }

    private EthSendTransaction setSaleLozmToken(Credentials systemCredentials) {
        return smartContractClient.callTransactionFunction(
                smartContractConfig.getContractAddress().getMintToken(),
                systemCredentials,
                new Function(
                        "setSaleLozmToken",
                        List.of(new Address(smartContractConfig.getContractAddress().getSaleToken())),
                        List.of(new TypeReference<Type>() {})
                )
        );
    }

    private void validateInitialSetting(EthSendTransaction ethSendTransaction, String errorMessage) {
        if (ethSendTransaction.hasError()) {
            throw new IllegalStateException(format("스마트 컨트랙트 초기 세팅에 실패하였습니다. 이유: %s", errorMessage));
        }
    }

    private List<ContractListVo.Detail> getTokens(Credentials credentials) {
        Function getTokensFunction = new Function(
                "getTokens",
                List.of(new Address(credentials.getAddress())),
                List.of(new TypeReference<DynamicArray<ContractListVo.Detail>>() {})
        );
        EthCall ethCall = smartContractClient.callViewFunction(smartContractConfig.getContractAddress().getMintToken(), credentials, getTokensFunction);

        List<Type> decodedOutputParameterList = FunctionReturnDecoder.decode(ethCall.getValue(), getTokensFunction.getOutputParameters());
        if (decodedOutputParameterList.isEmpty()) {
            throw new BadRequestException(CustomExceptionType.INVALID_REQUEST_PARAMETERS);
        }

        List<ContractListVo.Detail> resultList;

        try {
            resultList = (List<ContractListVo.Detail>) decodedOutputParameterList.get(0).getValue();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR, e);
        }

        return resultList;
    }

}
