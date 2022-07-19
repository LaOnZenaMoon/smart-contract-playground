package me.lozm.app.contract.client;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lozm.global.config.IpfsConfig;
import me.lozm.utils.exception.CustomExceptionType;
import me.lozm.utils.exception.InternalServerException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class IpfsClientImpl implements IpfsClient {

    private final IpfsConfig ipfsConfig;


    @Override
    public Multihash add(File file) {
        try {
            IPFS ipfs = new IPFS(ipfsConfig.getAddress());

            NamedStreamable.ByteArrayWrapper byteArrayWrapper = new NamedStreamable.ByteArrayWrapper(
                    file.getName(), Files.readAllBytes(file.toPath()));

            List<MerkleNode> addList = ipfs.add(byteArrayWrapper, true);
            if (addList.isEmpty()) {
                throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_IPFS);
            }

            log.info(format(ipfsConfig.getPrefixUrl(), addList.get(0).hash));

            return addList.get(0).hash;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new InternalServerException(CustomExceptionType.INTERNAL_SERVER_ERROR_IPFS, e);
        }
    }

}
