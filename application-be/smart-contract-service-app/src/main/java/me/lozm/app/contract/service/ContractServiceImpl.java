package me.lozm.app.contract.service;

import io.ipfs.multihash.Multihash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lozm.app.contract.client.IpfsClient;
import me.lozm.app.contract.client.SmartContractClient;
import me.lozm.app.contract.client.Web3jWrapperFunction;
import me.lozm.app.contract.code.TokenSearchType;
import me.lozm.app.contract.code.TokenStatus;
import me.lozm.app.contract.vo.ContractBuyVo;
import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import me.lozm.app.contract.vo.ContractSellVo;
import me.lozm.app.user.service.UserService;
import me.lozm.global.config.IpfsConfig;
import me.lozm.global.config.SmartContractConfig;
import me.lozm.utils.exception.CustomExceptionType;
import me.lozm.utils.exception.InternalServerException;
import me.lozm.web3j.MintLozmToken;
import me.lozm.web3j.SaleLozmToken;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final IpfsClient ipfsClient;
    private final SmartContractClient smartContractClient;
    private final SmartContractConfig smartContractConfig;
    private final IpfsConfig ipfsConfig;
    private final UserService userService;


    @Override
    public ContractMintVo.Response mintToken(ContractMintVo.Request requestVo) {
        Multihash multihash = ipfsClient.add(requestVo.getUploadFile());
        mintToken(requestVo, multihash);
        return new ContractMintVo.Response(format(ipfsConfig.getPrefixUrl(), multihash));
    }

    @Override
    public ContractListVo.Response getTokens(ContractListVo.Request requestVo) {
        final String walletAddress = requestVo.getWalletAddress();
        final TokenSearchType tokenSearchType = requestVo.getTokenSearchType();

        Credentials systemCredentials = Credentials.create(smartContractConfig.getEoa().getSystemPrivateKey());

        List<ContractListVo.Detail> resultList;
        if (tokenSearchType == TokenSearchType.ON_SALE) {
            if (isBlank(walletAddress)) {
                resultList = getTokensOnSale(systemCredentials);
            } else {
                resultList = getTokensByWalletAddress(systemCredentials, walletAddress).stream()
                        .filter(vo -> vo.getTokenStatus() == TokenStatus.SALE)
                        .collect(toList());
            }
        } else if (tokenSearchType == TokenSearchType.PRIVATE) {
            resultList = getTokensByWalletAddress(systemCredentials, walletAddress);
        } else {
            throw new IllegalArgumentException(format("지원하지 않는 토큰 검색 유형입니다. 토큰 검색 유형: %s", tokenSearchType));
        }

        return new ContractListVo.Response(resultList);
    }

    @Override
    public ContractSellVo.Response sellToken(ContractSellVo.Request requestVo) {
        Credentials credentialsUsingWallet = userService.getCredentialsUsingWallet(requestVo.getLoginId(), requestVo.getPassword());

        setApprovalForAll(credentialsUsingWallet);

        TransactionReceipt sellTokenTransactionReceipt = sellToken(requestVo, credentialsUsingWallet);

        return new ContractSellVo.Response(sellTokenTransactionReceipt.getTransactionHash());
    }

    private void setApprovalForAll(Credentials credentialsUsingWallet) {
        Web3jWrapperFunction<Web3j, TransactionReceipt> function = web3j ->
                getMintLozmTokenInstance(credentialsUsingWallet, web3j)
                        .setApprovalForAll(smartContractConfig.getContractAddress().getSaleToken(), true)
                        .sendAsync()
                        .get();

        TransactionReceipt transactionReceipt = smartContractClient.callFunction(function);
        if (!transactionReceipt.isStatusOK()) {
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_SMART_CONTRACT);
        }
    }

    @NotNull
    private TransactionReceipt sellToken(ContractSellVo.Request requestVo, Credentials credentialsUsingWallet) {
        Web3jWrapperFunction<Web3j, TransactionReceipt> sellTokenFunction = web3j ->
                getSaleLozmTokenInstance(credentialsUsingWallet, web3j)
                        .sellToken(requestVo.getTokenId(), requestVo.getTokenPrice())
                        .sendAsync()
                        .get();

        TransactionReceipt transactionReceipt = smartContractClient.callFunction(sellTokenFunction);
        if (!transactionReceipt.isStatusOK()) {
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_SMART_CONTRACT);
        }
        return transactionReceipt;
    }

    @Override
    public ContractBuyVo.Response buyToken(ContractBuyVo.Request requestVo) {
        Credentials credentialsUsingWallet = userService.getCredentialsUsingWallet(requestVo.getLoginId(), requestVo.getPassword());

        Web3jWrapperFunction<Web3j, TransactionReceipt> function = web3j ->
                getSaleLozmTokenInstance(credentialsUsingWallet, web3j)
                        .buyToken(requestVo.getTokenId(), requestVo.getTokenPrice())
                        .sendAsync()
                        .get();

        TransactionReceipt transactionReceipt = smartContractClient.callFunction(function);
        if (!transactionReceipt.isStatusOK()) {
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_SMART_CONTRACT);
        }

        return new ContractBuyVo.Response(transactionReceipt.getTransactionHash());
    }

    @NotNull
    @Override
    public MintLozmToken getMintLozmTokenInstance(Credentials senderCredentials, Web3j web3j) {
        return MintLozmToken.load(smartContractConfig.getContractAddress().getMintToken(), web3j,
                senderCredentials, smartContractConfig.getGasProviderInstance());
    }

    @NotNull
    @Override
    public SaleLozmToken getSaleLozmTokenInstance(Credentials senderCredentials, Web3j web3j) {
        return SaleLozmToken.load(smartContractConfig.getContractAddress().getSaleToken(), web3j,
                senderCredentials, smartContractConfig.getGasProviderInstance());
    }

    private void mintToken(ContractMintVo.Request requestVo, Multihash multihash) {
        Credentials credentialsUsingWallet = userService.getCredentialsUsingWallet(requestVo.getLoginId(), requestVo.getPassword());

        Web3jWrapperFunction<Web3j, TransactionReceipt> function = web3j ->
                getMintLozmTokenInstance(credentialsUsingWallet, web3j)
                        .mintToken(multihash.toString())
                        .sendAsync()
                        .get();

        TransactionReceipt transactionReceipt = smartContractClient.callFunction(function);
        if (!transactionReceipt.isStatusOK()) {
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_SMART_CONTRACT);
        }
    }

    private List<ContractListVo.Detail> getTokensByWalletAddress(Credentials senderCredentials, String walletAddress) {
        Web3jWrapperFunction<Web3j, List<MintLozmToken.TokenData>> function = web3j ->
                getMintLozmTokenInstance(senderCredentials, web3j)
                        .getTokens(walletAddress)
                        .sendAsync()
                        .get();

        List<MintLozmToken.TokenData> responseList = smartContractClient.callFunction(function);
        return responseList.stream()
                .map(vo -> new ContractListVo.Detail(vo.tokenId, format(ipfsConfig.getPrefixUrl(), vo.tokenUrl), vo.tokenPrice))
                .collect(toList());
    }

    private List<ContractListVo.Detail> getTokensOnSale(Credentials senderCredentials) {
        Web3jWrapperFunction<Web3j, List<SaleLozmToken.TokenData>> function = web3j ->
                getSaleLozmTokenInstance(senderCredentials, web3j)
                        .getTokensOnSale()
                        .sendAsync()
                        .get();

        List<SaleLozmToken.TokenData> responseList = smartContractClient.callFunction(function);
        return responseList.stream()
                .map(vo -> new ContractListVo.Detail(vo.tokenId, format(ipfsConfig.getPrefixUrl(), vo.tokenUrl), vo.tokenPrice))
                .collect(toList());
    }

}
