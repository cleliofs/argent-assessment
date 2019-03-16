pragma solidity ^0.4.24;

// ----------------------------------------------------------------------------
// Wallet smart contract
// ----------------------------------------------------------------------------
contract Wallet {
    address owner;
    modifier onlyOwner {
        require(msg.sender == owner);
        _;
    }

    constructor() public {
        owner = msg.sender;
    }

    function transfer(address _dest, uint _value) public onlyOwner {
        _dest.transfer(_value);
    }

    function changeOwner(address _newOwner) public onlyOwner {
        owner = _newOwner;
    }
}
