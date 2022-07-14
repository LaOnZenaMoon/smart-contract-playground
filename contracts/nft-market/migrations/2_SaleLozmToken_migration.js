const SaleLozmToken = artifacts.require("SaleLozmToken");

module.exports = function (deployer) {
  var MintLozmTokenAddress = '0x6fde4F058843a940554a1B77D8c05014b3a3B604';
  deployer.deploy(SaleLozmToken, MintLozmTokenAddress);
};
