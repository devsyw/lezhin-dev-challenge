package com.lezhin.lezhinchallenge.common.exception.custom;

import com.lezhin.lezhinchallenge.common.exception.ErrorCode;

/**
 * 구매 내역을 찾을 수 없을 때 발생하는 예외
 */
public class PurchaseNotFoundException extends EntityNotFoundException {

    public PurchaseNotFoundException() {
        super(ErrorCode.PURCHASE_NOT_FOUND);
    }

    public PurchaseNotFoundException(String message) {
        super(ErrorCode.PURCHASE_NOT_FOUND, message);
    }
}