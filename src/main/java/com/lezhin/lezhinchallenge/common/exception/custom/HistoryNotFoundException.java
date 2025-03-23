package com.lezhin.lezhinchallenge.common.exception.custom;

import com.lezhin.lezhinchallenge.common.exception.ErrorCode;

/**
 * 조회 이력을 찾을 수 없을 때 발생하는 예외
 */
public class HistoryNotFoundException extends EntityNotFoundException {

    public HistoryNotFoundException() {
        super(ErrorCode.HISTORY_NOT_FOUND);
    }

    public HistoryNotFoundException(String message) {
        super(ErrorCode.HISTORY_NOT_FOUND, message);
    }
}