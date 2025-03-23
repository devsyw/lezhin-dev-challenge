package com.lezhin.lezhinchallenge.common.exception.custom;

import com.lezhin.lezhinchallenge.common.exception.ErrorCode;

/**
 * 작품을 찾을 수 없을 때 발생하는 예외
 */
public class WorkNotFoundException extends EntityNotFoundException {

    public WorkNotFoundException() {
        super(ErrorCode.WORK_NOT_FOUND);
    }

    public WorkNotFoundException(String message) {
        super(ErrorCode.WORK_NOT_FOUND, message);
    }
}