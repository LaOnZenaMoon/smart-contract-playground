package me.lozm.app.contract.service;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lozm.app.contract.vo.ContractMintVo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;

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
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    @Value("${ipfs.prefix-url}")
    private String ipfsPrefixUrl;

    @Value("${ipfs.address}")
    private String ipfsAddress;

    @Value("${smart-contracts.mint-token-contract-address}")
    private String mintTokenContractAddress;

    @Value("${smart-contracts.sale-token-contract-address}")
    private String saleTokenContractAddress;


    @Override
    public ContractMintVo.Response mintToken(ContractMintVo.Request requestVo) {
        validateSmartContractAddress();

        Multihash multihash = addFileOnIpfs(requestVo.getFile());

        Function mintTokenFunction = new Function(
                "mintToken",
                List.of(new Utf8String(multihash.toString())),
                List.of(new TypeReference<Type>() {}));

        createFunctionCallTransaction(
                Credentials.create(requestVo.getPrivateKey()),
                Web3j.build(new HttpService()),
                mintTokenFunction
        );

        return new ContractMintVo.Response(String.format(ipfsPrefixUrl, multihash));
    }

    private Multihash addFileOnIpfs(File file) {
        try {
            IPFS ipfs = new IPFS(ipfsAddress);
            NamedStreamable.ByteArrayWrapper byteArrayWrapper = new NamedStreamable.ByteArrayWrapper(
                    file.getName(), Files.readAllBytes(file.toPath()));
            List<MerkleNode> addList = ipfs.add(byteArrayWrapper, true);

            if (addList.size() == 0) {
                throw new IllegalArgumentException("IPFS 파일 등록에 실패하였습니다.");
            }

            log.info(format(ipfsPrefixUrl, addList.get(0).hash));

            return addList.get(0).hash;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private EthSendTransaction createFunctionCallTransaction(Credentials credentials, Web3j web3j, Function function) {
        try {
            Transaction functionCallTransaction = Transaction.createFunctionCallTransaction(
                    credentials.getAddress(), // from
                    web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getTransactionCount(), // nonce
                    Transaction.DEFAULT_GAS, // gasPrice
                    new BigInteger("1537000"), //gasLimit
                    mintTokenContractAddress, // to
                    FunctionEncoder.encode(function) // data
            );

            EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(functionCallTransaction).sendAsync().get();
            if (ethSendTransaction.hasError()) {
                throw new IllegalArgumentException(format("스마트 컨트랙트의 function 호출에 실패하였습니다. 사유: %s", ethSendTransaction.getError()));
            }

            return ethSendTransaction;
        } catch (IOException | ExecutionException | InterruptedException e) {
            log.error(e.getMessage());
            e.printStackTrace();
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
