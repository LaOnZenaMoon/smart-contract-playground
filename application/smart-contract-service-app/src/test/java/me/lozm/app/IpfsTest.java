package me.lozm.app;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import lombok.extern.slf4j.Slf4j;
import me.lozm.global.config.IpfsConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("테스트 시, IPFS Daemon (container) 필요")
@Slf4j
@SpringBootTest
class IpfsTest {

    @Autowired
    private IpfsConfig ipfsConfig;


    @DisplayName("IPFS 파일 생성 성공")
    @ParameterizedTest(name = "{index}. {displayName} 입력값={0}")
    @ValueSource(strings = {"hello.txt", "sample.jpg"})
    void addFileOnIpfs_success(final String fileName) throws IOException {
        // Given
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
        ClassPathResource classPathResource = new ClassPathResource("ipfs/" + fileName);
        NamedStreamable.ByteArrayWrapper byteArrayWrapper = new NamedStreamable.ByteArrayWrapper(classPathResource.getFilename(), Files.readAllBytes(classPathResource.getFile().toPath()));

        // When
        List<MerkleNode> addList = ipfs.add(byteArrayWrapper, true);
        for (MerkleNode merkleNode : addList) {
            log.info(format("IPFS Node: " + ipfsConfig.getPrefixUrl(), merkleNode.hash));
        }

        // Then
        assertTrue(addList.size() > 0);
        assertEquals(classPathResource.getFilename(), addList.get(0).name.get());
    }

}
