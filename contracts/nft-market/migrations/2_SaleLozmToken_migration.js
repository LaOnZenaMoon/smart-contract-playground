const MintLozmToken = artifacts.require("MintLozmToken");
const SaleLozmToken = artifacts.require("SaleLozmToken");

module.exports = function (deployer) {
  deployer.deploy(SaleLozmToken, MintLozmToken.address);
};
