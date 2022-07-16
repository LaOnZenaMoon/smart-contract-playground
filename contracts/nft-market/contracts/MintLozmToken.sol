// SPDX-License-Identifier: MIT

pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC721/extensions/ERC721Enumerable.sol";
import "./SaleLozmToken.sol";

contract MintLozmToken is ERC721Enumerable {
    mapping(uint256 => uint256) public mintedTokens;

    SaleLozmToken public saleLozmToken;

    struct TokenData {
        uint256 tokenId;
        uint256 tokenUrl;
        uint256 tokenPrice;
    }

    constructor() ERC721("laonzenamoon", "LOZM") {}

    function mintToken() public {
        uint256 tokenId = totalSupply() + 1;

        uint256 tokenUrl = uint256(keccak256(abi.encodePacked(block.timestamp, msg.sender, tokenId))) % 5 + 1;

        mintedTokens[tokenId] = tokenUrl;

        _mint(msg.sender, tokenId);
    }

    function setSaleLozmToken(address _saleLozmToken) public {
        saleLozmToken = SaleLozmToken(_saleLozmToken);
    }

    function getTokens(address _tokenOwner) view public returns (TokenData[] memory) {
        uint256 balanceLength = balanceOf(_tokenOwner);

        require(balanceLength == 0, "Owner did not have toekn.");

        TokenData[] memory tokenDataArray = new TokenData[](balanceLength);

        for (uint256 i = 0; i < balanceLength; i++) {
            uint256 tokenId = tokenOfOwnerByIndex(_tokenOwner, i);
            uint256 tokenUrl = mintedTokens[tokenId];
            uint256 tokenPrice = saleLozmToken.getTokenPrice(tokenId);

            tokenDataArray[i] = TokenData(tokenId, tokenUrl, tokenPrice);
        }

        return tokenDataArray;
    }
}