const MintLozmToken = artifacts.require("MintLozmToken");

module.exports = function (deployer) {
  deployer.deploy(MintLozmToken);
};
