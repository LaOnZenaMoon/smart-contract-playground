package me.lozm.app.contract.client;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import lombok.extern.slf4j.Slf4j;
import me.lozm.utils.exception.CustomExceptionType;
import me.lozm.utils.exception.InternalServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class IpfsClientImpl implements IpfsClient {

    @Value("${ipfs.prefix-url}")
    private String ipfsPrefixUrl;

    @Value("${ipfs.address}")
    private String ipfsAddress;

    @Override
    public Multihash add(File file) {
        try {
            IPFS ipfs = new IPFS(ipfsAddress);

            NamedStreamable.ByteArrayWrapper byteArrayWrapper = new NamedStreamable.ByteArrayWrapper(
                    file.getName(), Files.readAllBytes(file.toPath()));

            List<MerkleNode> addList = ipfs.add(byteArrayWrapper, true);
            if (addList.isEmpty()) {
                throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_IPFS);
            }

            log.info(format(ipfsPrefixUrl, addList.get(0).hash));

            return addList.get(0).hash;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_IPFS, e);
        }
    }

}
