package com.lezhin.lezhinchallenge.common.exception.custom;

import com.lezhin.lezhinchallenge.common.exception.BaseException;
import com.lezhin.lezhinchallenge.common.exception.ErrorCode;

/**
 * 중복된 값이 존재할 때 발생하는 예외
 */
public class DuplicateValueException extends BaseException {

    public DuplicateValueException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DuplicateValueException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
