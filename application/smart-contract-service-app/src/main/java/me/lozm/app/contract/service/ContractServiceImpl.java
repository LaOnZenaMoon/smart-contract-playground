package me.lozm.app.contract.service;

import io.ipfs.multihash.Multihash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lozm.app.contract.client.IpfsClient;
import me.lozm.app.contract.client.SmartContractClient;
import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final IpfsClient ipfsClient;
    private final SmartContractClient smartContractClient;

    @Value("${ipfs.prefix-url}")
    private String ipfsPrefixUrl;

    @Value("${smart-contracts.mint-token-contract-address}")
    private String mintTokenContractAddress;

    @Value("${smart-contracts.sale-token-contract-address}")
    private String saleTokenContractAddress;

    @PostConstruct
    public void initialize() {
        initializeContracts();
    }

    private void initializeContracts() {
        //TODO 스마트 컨트랙트 초기 세팅
        // 1. MintLozmToken.setApprovalForAll(SaleLozmToken.sol address, true)
        // 2. MintLozmToken.isApprovedForAll(EOA address, SaleLozmToken.sol address)
        // 3. MintLozmToken.setSaleLozmToken(EOA address)
    }

    @Override
    public ContractMintVo.Response mintToken(ContractMintVo.Request requestVo) {
        Multihash multihash = ipfsClient.add(requestVo.getFile());

        Function mintTokenFunction = new Function(
                "mintToken",
                List.of(new Utf8String(multihash.toString())),
                List.of(new TypeReference<Type>() {
                })
        );
        smartContractClient.callTransaction(mintTokenContractAddress, Credentials.create(requestVo.getPrivateKey()), mintTokenFunction);

        return new ContractMintVo.Response(String.format(ipfsPrefixUrl, multihash));
    }

    @Override
    public ContractListVo.Response getTokens(ContractListVo.Request requestVo) {
        Credentials credentials = Credentials.create(requestVo.getPrivateKey());

        setSaleLozmToken(requestVo.getPrivateKey());

        List<ContractListVo.Detail> resultList = getTokens(credentials);

        return new ContractListVo.Response(resultList);
    }

    @Override
    public EthSendTransaction setSaleLozmToken(String privateKey) {
        if (isBlank(privateKey)) {
            throw new IllegalArgumentException(format("EOA 개인키는 비어있을 수 없습니다."));
        }

        Function setSaleLozmTokenFunction = new Function(
                "setSaleLozmToken",
                List.of(new Address(saleTokenContractAddress)),
                List.of(new TypeReference<Type>() {})
        );
        return smartContractClient.callTransaction(mintTokenContractAddress, Credentials.create(privateKey), setSaleLozmTokenFunction);
    }

    private List<ContractListVo.Detail> getTokens(Credentials credentials) {
        Function getTokensFunction = new Function(
                "getTokens",
                List.of(new Address(credentials.getAddress())),
                List.of(new TypeReference<DynamicArray<ContractListVo.Detail>>() {})
        );
        EthCall ethCall = smartContractClient.callViewFunction(mintTokenContractAddress, credentials, getTokensFunction);
        List<Type> decodedOutputParameterList = FunctionReturnDecoder.decode(ethCall.getValue(), getTokensFunction.getOutputParameters());
        if (decodedOutputParameterList.isEmpty()) {
            throw new IllegalStateException();
        }
        List<ContractListVo.Detail> resultList = (List<ContractListVo.Detail>) decodedOutputParameterList.get(0).getValue();
        return resultList;
    }

}
