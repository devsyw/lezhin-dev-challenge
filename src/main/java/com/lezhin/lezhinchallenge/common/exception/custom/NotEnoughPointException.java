package com.lezhin.lezhinchallenge.common.exception.custom;

import com.lezhin.lezhinchallenge.common.exception.BaseException;
import com.lezhin.lezhinchallenge.common.exception.ErrorCode;

/**
 * 포인트가 부족할 때 발생하는 예외
 */
public class NotEnoughPointException extends BaseException {

    public NotEnoughPointException() {
        super(ErrorCode.NOT_ENOUGH_POINT);
    }

    public NotEnoughPointException(String message) {
        super(ErrorCode.NOT_ENOUGH_POINT, message);
    }
}