package com.argent.assessment.controller;

import com.argent.assessment.data.Address;
import com.argent.assessment.dto.TrustIndicatorDto;
import com.argent.assessment.dto.WalletInfoDto;
import com.argent.assessment.dto.WalletTransferRequestDto;
import com.argent.assessment.dto.WalletTransferResponseDto;
import com.argent.assessment.service.TrustIndicatorWallet;
import com.argent.assessment.service.ethereum.Web3JClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

import java.util.concurrent.ExecutionException;

import static com.argent.assessment.dto.WalletTransferResponseDto.TransferStatus.PENDING;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Spring MVC Rest controller to provide API endpoints to interact with Wallet.
 *
 */
@Slf4j
@RestController
public class WalletController {

    private TrustIndicatorWallet trustIndicatorWallet;

    @Autowired
    public WalletController(TrustIndicatorWallet trustIndicatorWallet) {
        this.trustIndicatorWallet = trustIndicatorWallet;
    }

    @RequestMapping(value = "/trustIndicatorFromOwnerAddress", method = GET, produces = APPLICATION_JSON_VALUE)
    public TrustIndicatorDto trustIndicatorFromOwnerAddress(@RequestParam(value = "addressTo") final String addressTo) {
        final int trustIndicator = trustIndicatorWallet.getTrustIndicatorToAddress(new Address(addressTo, false));
        return new TrustIndicatorDto(trustIndicatorWallet.getAddressOwner(), addressTo, trustIndicator);
    }

    @RequestMapping(value = "/trustIndicatorBetweenAddresses", method = GET, produces = APPLICATION_JSON_VALUE)
    public TrustIndicatorDto trustIndicatorBetweenAddresses(@RequestParam(value = "addressFrom") final String addressFrom,
                                                            @RequestParam(value = "addressTo") final String addressTo) {
        final int trustIndicator = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(
                new Address(addressFrom, false),
                new Address(addressTo, false));
        return new TrustIndicatorDto(addressFrom, addressTo, trustIndicator);
    }

    @RequestMapping(value = "/transfer/{addressTo}", method = POST, produces = APPLICATION_JSON_VALUE)
    public WalletTransferResponseDto transfer(@PathVariable("addressTo") String addressTo,
                                              @RequestBody WalletTransferRequestDto walletTransferRequestDto) {
        return WalletTransferResponseDto.builder()
                .walletInfo(WalletInfoDto.fromWallet(trustIndicatorWallet))
                .addressTo(addressTo)
                .value(walletTransferRequestDto.value)
                .transferStatus(PENDING)
                .build();
    }

    @RequestMapping(value = "/info", method = GET, produces = APPLICATION_JSON_VALUE)
    public WalletInfoDto walletInfo() {
        return WalletInfoDto.fromWallet(trustIndicatorWallet);
    }

    @RequestMapping(value = "/client/version", method = GET)
    public String getClientVersion() throws ExecutionException, InterruptedException {
        Web3j web3 = new Web3JClient().getWeb3j();
        // wait on async block
        Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
        return web3ClientVersion.getWeb3ClientVersion();
    }

}
