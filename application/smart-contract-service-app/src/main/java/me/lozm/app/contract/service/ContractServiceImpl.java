package me.lozm.app.contract.service;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import lombok.extern.slf4j.Slf4j;
import me.lozm.app.contract.vo.ContractListVo;
import me.lozm.app.contract.vo.ContractMintVo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class ContractServiceImpl implements ContractService {

    @Value("${ipfs.prefix-url}")
    private String ipfsPrefixUrl;

    @Value("${ipfs.address}")
    private String ipfsAddress;

    @Value("${smart-contracts.mint-token-contract-address}")
    private String mintTokenContractAddress;

    @Value("${smart-contracts.sale-token-contract-address}")
    private String saleTokenContractAddress;

    private Web3j web3j;

    @PostConstruct
    public void init() {
        web3j = Web3j.build(new HttpService());
        validateSmartContractAddress();
    }

    @Override
    public ContractMintVo.Response mintToken(ContractMintVo.Request requestVo) {
        Multihash multihash = addFileOnIpfs(requestVo.getFile());

        Function mintTokenFunction = new Function(
                "mintToken",
                List.of(new Utf8String(multihash.toString())),
                List.of(new TypeReference<Type>() {
                })
        );
        callTransaction(mintTokenContractAddress, Credentials.create(requestVo.getPrivateKey()), mintTokenFunction);

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
        return callTransaction(mintTokenContractAddress, Credentials.create(privateKey), setSaleLozmTokenFunction);
    }

    @Override
    public TransactionReceipt getTransactionReceipt(String transactionHash) {
        EthGetTransactionReceipt ethGetTransactionReceipt;
        try {
            ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).sendAsync().get();
        } catch (ExecutionException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        if (ethGetTransactionReceipt.getTransactionReceipt().isEmpty()) {
            throw new IllegalArgumentException(format("잘못된 transaction 값입니다. transaction hash: %s", transactionHash));
        }

        return ethGetTransactionReceipt.getTransactionReceipt().get();
    }

    private List<ContractListVo.Detail> getTokens(Credentials credentials) {
        Function getTokensFunction = new Function(
                "getTokens",
                List.of(new Address(credentials.getAddress())),
                List.of(new TypeReference<DynamicArray<ContractListVo.Detail>>() {})
        );
        EthCall ethCall = callView(mintTokenContractAddress, credentials, getTokensFunction);
        List<Type> decodedOutputParameterList = FunctionReturnDecoder.decode(ethCall.getValue(), getTokensFunction.getOutputParameters());
        if (decodedOutputParameterList.isEmpty()) {
            throw new IllegalStateException();
        }
        List<ContractListVo.Detail> resultList = (List<ContractListVo.Detail>) decodedOutputParameterList.get(0).getValue();
        return resultList;
    }

    @NotNull
    private EthCall callView(String contractAddress, Credentials credentials, Function function) {
        if (isBlank(contractAddress)) {
            throw new IllegalArgumentException("스마트 컨트랙트 주소는 비어있을 수 없습니다.");
        }

        try {
            Transaction functionCallTransaction = Transaction.createEthCallTransaction(
                    credentials.getAddress(), // from
                    contractAddress, // to
                    FunctionEncoder.encode(function) // data
            );

            EthCall ethCall = web3j.ethCall(functionCallTransaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            if (ethCall.hasError()) {
                throw new IllegalArgumentException(format("스마트 컨트랙트의 view function 호출에 실패하였습니다. 대상: %s > %s", contractAddress, function.getName()));
            }

            return ethCall;
        } catch (ExecutionException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private EthSendTransaction callTransaction(String contractAddress, Credentials credentials, Function function) {
        if (isBlank(contractAddress)) {
            throw new IllegalArgumentException("스마트 컨트랙트 주소는 비어있을 수 없습니다.");
        }

        try {
            Transaction functionCallTransaction = Transaction.createFunctionCallTransaction(
                    credentials.getAddress(), // from
                    web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getTransactionCount(), // nonce
                    Transaction.DEFAULT_GAS, // gasPrice
                    new BigInteger("1537000"), //gasLimit
                    contractAddress, // to
                    FunctionEncoder.encode(function) // data
            );

            EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(functionCallTransaction).sendAsync().get();
            if (ethSendTransaction.hasError()) {
                throw new IllegalArgumentException(format("스마트 컨트랙트의 function 호출에 실패하였습니다. 대상: %s > %s", contractAddress, function.getName()));
            }

            return ethSendTransaction;
        } catch (IOException | ExecutionException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private Multihash addFileOnIpfs(File file) {
        try {
            IPFS ipfs = new IPFS(ipfsAddress);
            NamedStreamable.ByteArrayWrapper byteArrayWrapper = new NamedStreamable.ByteArrayWrapper(
                    file.getName(), Files.readAllBytes(file.toPath()));
            List<MerkleNode> addList = ipfs.add(byteArrayWrapper, true);

            if (addList.isEmpty()) {
                throw new IllegalArgumentException("IPFS 파일 등록에 실패하였습니다.");
            }

            log.info(format(ipfsPrefixUrl, addList.get(0).hash));

            return addList.get(0).hash;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void validateSmartContractAddress() {
        if (isBlank(mintTokenContractAddress) || StringUtils.equals(mintTokenContractAddress, "none")) {
            throw new IllegalStateException("MintLozmToken 스마트 컨트랙트의 address 값이 올바르지 않습니다.");
        }

        if (isBlank(saleTokenContractAddress) || StringUtils.equals(saleTokenContractAddress, "none")) {
            throw new IllegalStateException("SaleLozmToken 스마트 컨트랙트의 address 값이 올바르지 않습니다.");
        }
    }

}
