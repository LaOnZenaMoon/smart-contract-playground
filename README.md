## smart-contract-playground
* Deal with smart contracts about Blockchain

## Project structure
### contracts
* The module for smart contracts

### application
#### smart-contract-service-app
* The server for smart-contract operations

## Skills
* Solidity
* OpenZeppelin
  ```
  $ npm install @openzeppelin/contracts
  ```
* Remix
  * Connect from Remix web IDE to localhost
    ```
    $ npm uninstall -g remixd
    $ yarn global add @remix-project/remixd
    $ remixd -s . --remix-ide https://remix.ethereum.org
    ```
* Truffle
  ```
  $ npm install -g truffle
  $ truffle compile
  # When compilation failed
  $ npm init
  $ npm install --save @openzeppelin/contracts
  $ truffle migrate --reset
  ```
* Ganache
  ```
  $ npm install -g ganache-cli
  ```
* IPFS
  * java-ipfs-http-client
  * [IPFS Daemon](https://docs.ipfs.io/install/)
  ```
  $ ipfs init
  $ ipfs daemon
  ```