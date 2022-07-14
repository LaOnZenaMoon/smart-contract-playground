// SPDX-License-Identifier: MIT

pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC721/extensions/ERC721Enumerable.sol";

contract MintLozmToken is ERC721Enumerable {
    mapping(uint256 => uint256) public mintedTokens;

    constructor() ERC721("laonzenamoon", "LOZM") {}

    function mintToken() public returns (uint256) {
        uint256 tokenId = totalSupply() + 1;

        uint256 tokenType = uint256(keccak256(abi.encodePacked(block.timestamp, msg.sender, tokenId))) % 5 + 1;

        mintedTokens[tokenId] = tokenType;

        _mint(msg.sender, tokenId);

        return tokenId;
    }
}