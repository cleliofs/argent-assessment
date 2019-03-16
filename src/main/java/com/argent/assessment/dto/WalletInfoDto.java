package com.argent.assessment.dto;

import com.argent.assessment.data.TrustIndicatorWallet;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
