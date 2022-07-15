const MintLozmToken = artifacts.require("MintLozmToken");
const SaleLozmToken = artifacts.require("SaleLozmToken");

module.exports = function (deployer) {
  // var MintLozmTokenAddress = '0x939190253608d9661e1E9c423Cc195A1e6d418fD';
  deployer.deploy(SaleLozmToken, MintLozmToken.address);
};
