package me.lozm.app.contract.client;

import io.ipfs.multihash.Multihash;

import java.io.File;

public interface IpfsClient {

    Multihash add(File file);

}
