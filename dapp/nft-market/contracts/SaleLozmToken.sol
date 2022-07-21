// SPDX-License-Identifier: MIT

pragma solidity ^0.8.0;

import "./MintLozmToken.sol";

contract SaleLozmToken {
    MintLozmToken public mintTokenAddress;

    mapping(uint256 => uint256) public tokenPrices;

    uint256[] public onSaleTokenArray;

    constructor(address _mintTokenAddress) {
        mintTokenAddress = MintLozmToken(_mintTokenAddress);
    }

    function sellToken(uint256 _tokenId, uint256 _price) public {
        address tokenOwner = mintTokenAddress.ownerOf(_tokenId);

        require(tokenOwner == msg.sender, "Caller is not token owner.");
        require(_price > 0, "Sale price is larger than 0.");
        require(tokenPrices[_tokenId] == 0, "The token is already on sale.");
        require(mintTokenAddress.isApprovedForAll(tokenOwner, address(this)), "Token owner did not approve token.");

        tokenPrices[_tokenId] = _price;

        onSaleTokenArray.push(_tokenId);
    }

    function purchaseToken(uint256 _tokenId) public payable {
        uint256 price = tokenPrices[_tokenId];
        address tokenOwner = mintTokenAddress.ownerOf(_tokenId);

        require(price > 0, "The token is not on sale.");
        require(price <= msg.value, "Caller send lower than price.");
        require(tokenOwner != msg.sender, "Caller is token owner.");

        payable(tokenOwner).transfer(msg.value);
        mintTokenAddress.safeTransferFrom(tokenOwner, msg.sender, _tokenId);

        tokenPrices[_tokenId] = 0;

        for (uint256 i = 0; i < onSaleTokenArray.length; i++) {
            if (tokenPrices[onSaleTokenArray[i]] == 0) {
                onSaleTokenArray[i] = onSaleTokenArray[onSaleTokenArray.length - 1];
                onSaleTokenArray.pop();
            }
        }
    }

    function getOnSaleTokenArrayLength() view public returns (uint256) {
        return onSaleTokenArray.length;
    }

    function getTokenPrice(uint256 _tokenId) view public returns (uint256) {
        return tokenPrices[_tokenId];
    }
}
