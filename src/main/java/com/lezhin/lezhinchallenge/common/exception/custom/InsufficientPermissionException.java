package com.lezhin.lezhinchallenge.common.exception.custom;

import com.lezhin.lezhinchallenge.common.exception.BaseException;
import com.lezhin.lezhinchallenge.common.exception.ErrorCode;

/**
 * 권한 부족 예외
 */
public class InsufficientPermissionException extends BaseException {

    public InsufficientPermissionException() {
        super(ErrorCode.INSUFFICIENT_PERMISSION);
    }

    public InsufficientPermissionException(String message) {
        super(ErrorCode.INSUFFICIENT_PERMISSION, message);
    }
}