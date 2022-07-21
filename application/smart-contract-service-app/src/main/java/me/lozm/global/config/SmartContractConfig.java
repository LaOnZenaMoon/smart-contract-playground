package me.lozm.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import javax.annotation.PostConstruct;
import java.math.BigInteger;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "smart-contracts")
public class SmartContractConfig {

    private String blockChainNetworkUrl;
    private String gasLimit;
    private ContractAddress contractAddress;
    private Eoa eoa;
    private StaticGasProvider gasProvider;


    @PostConstruct
    public void initialize() {
        gasProvider = new StaticGasProvider(DefaultGasProvider.GAS_PRICE, new BigInteger(gasLimit));
    }


    @Getter
    @Setter
    public static class ContractAddress {
        private String mintToken;
        private String saleToken;
    }

    @Getter
    @Setter
    public static class Eoa {

        private String systemPrivateKey;
        private String samplePrivateKey;
    }

    public Web3j createWeb3jInstance() {
        final String blockChainNetworkUrl = defaultIfBlank(this.blockChainNetworkUrl, HttpService.DEFAULT_URL);
        return Web3j.build(new HttpService(blockChainNetworkUrl));
    }

    public ContractGasProvider getGasProviderInstance() {
        return gasProvider;
    }

}
