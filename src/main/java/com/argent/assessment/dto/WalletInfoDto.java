package com.argent.assessment.dto;

import com.argent.assessment.service.TrustIndicatorWallet;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletInfoDto {

    public String addressOwner;
    public int trustIndicatorMinimum;

    public static WalletInfoDto fromWallet(TrustIndicatorWallet wallet) {
        return WalletInfoDto.builder()
                .addressOwner(wallet.getAddressOwner())
                .trustIndicatorMinimum(wallet.getTrustIndicatorMinimum())
                .build();
    }

}
