package me.lozm.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "smart-contracts")
public class SmartContractConfig {

    private String gasLimit;
    private ContractAddress contractAddress;
    private Eoa eoa;

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

}
