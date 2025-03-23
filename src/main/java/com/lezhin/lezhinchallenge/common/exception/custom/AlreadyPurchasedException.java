package com.lezhin.lezhinchallenge.common.exception.custom;

import com.lezhin.lezhinchallenge.common.exception.ErrorCode;

/**
 * 이미 구매한 작품일 때 발생하는 예외
 */
public class AlreadyPurchasedException extends DuplicateValueException {

    public AlreadyPurchasedException() {
        super(ErrorCode.ALREADY_PURCHASED);
    }

    public AlreadyPurchasedException(String message) {
        super(ErrorCode.ALREADY_PURCHASED, message);
    }
}
