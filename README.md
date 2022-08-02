# smart-contract-playground
* Deal with smart contracts about Blockchain

# Project structure
## dapp
* The module for smart contracts

## application-fe


## application-be
### smart-contract-service-app
* The server for smart-contract operations
* If you deploy smart contracts at the first time, needs to set up initialize options to 'true' on application.yml file.
  ```
  smart-contracts:
    initialize: true
  ```
* REST API Documentation
  ```
  http://{{host}}:8000/swagger-ui.html
  ```

### core-utils
* The module for common utilities

### core-web
* The module for common web configurations

### domain-rds
* The module for applications' models and entities about RDBMS


# Skills
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
* Java 11
* Spring Boot 2.5.x
* Spring Security
* Swagger
  ```
  http://{{host}}:8000/swagger-ui/index.html
  ```
* [web3j-gradle-plugin](https://github.com/web3j/web3j-gradle-plugin)
* [solidity-gradle-plugin](https://github.com/web3j/solidity-gradle-plugin)