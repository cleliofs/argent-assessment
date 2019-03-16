package com.argent.assessment.dto;

import lombok.Data;

@Data
public class TrustIndicatorDto {

    private final String addressFrom;
    private final String addressTo;
    private final int trustIndicator;

}
