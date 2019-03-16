package com.argent.assessment.controller;

import com.argent.assessment.data.Address;
import com.argent.assessment.data.TrustIndicator;
import com.argent.assessment.data.TrustIndicatorWallet;
import com.argent.assessment.dto.WalletInfoDto;
import com.argent.assessment.dto.WalletTransferRequestDto;
import com.argent.assessment.dto.WalletTransferResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.argent.assessment.dto.WalletTransferResponseDto.TransferStatus.PENDING;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class WalletController {

    private TrustIndicatorWallet trustIndicatorWallet;

    @Autowired
    public WalletController(TrustIndicatorWallet trustIndicatorWallet) {
        this.trustIndicatorWallet = trustIndicatorWallet;
    }

    @RequestMapping(value = "/trustIndicatorFromOwnerAddress", produces = APPLICATION_JSON_VALUE)
    public TrustIndicator trustIndicatorFromOwnerAddress(@RequestParam(value = "addressTo") final String addressTo) {
        final int trustLevel = trustIndicatorWallet.getTrustIndicatorToAddress(new Address(addressTo, false));
        return new TrustIndicator(trustIndicatorWallet.getAddressOwner(), addressTo, trustLevel);
    }

    @RequestMapping(value = "/trustIndicatorFromTwoAddresses", produces = APPLICATION_JSON_VALUE)
    public TrustIndicator trustIndicatorFromTwoAddresses(@RequestParam(value = "addressFrom") final String addressFrom,
                                                         @RequestParam(value = "addressTo") final String addressTo) {
        final int trustLevel = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(
                new Address(addressFrom, false),
                new Address(addressTo, false));
        return new TrustIndicator(trustIndicatorWallet.getAddressOwner(), addressTo, trustLevel);
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

    @RequestMapping(value = "/info", produces = APPLICATION_JSON_VALUE)
    public WalletInfoDto walletInfo() {
        return WalletInfoDto.fromWallet(trustIndicatorWallet);
    }
}
