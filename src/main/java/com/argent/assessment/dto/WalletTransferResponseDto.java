package com.argent.assessment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WalletTransferResponseDto {

    public enum TransferStatus {
        SUCCESS, PENDING, FAILURE;
    }

    public WalletInfoDto walletInfo;
    public String addressTo;
    public int value;
    public TransferStatus transferStatus;

}
