package com.lezhin.lezhinchallenge.common.exception.custom;

import com.lezhin.lezhinchallenge.common.exception.BaseException;
import com.lezhin.lezhinchallenge.common.exception.ErrorCode;

/**
 * 엔티티를 찾을 수 없을 때 발생하는 예외
 */
public class EntityNotFoundException extends BaseException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EntityNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}