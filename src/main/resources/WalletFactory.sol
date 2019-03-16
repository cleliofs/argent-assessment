pragma solidity ^0.4.24;

// ----------------------------------------------------------------------------
// WalletFactory smart contract
// ----------------------------------------------------------------------------

contract WalletFactory {
    event WalletCreated(address indexed _wallet);
    event WalletAssigned(address indexed _wallet, address indexed _owner);

    function createWallet() public {
        Wallet wallet = new Wallet();
        emit WalletCreated(wallet);
    }

    function assignWallet(address _wallet, address _owner) public {
        Wallet(_Wallet).changeOwner(_owner);
        emit WalletAssigned(_wallet, _owner);
    }
}
