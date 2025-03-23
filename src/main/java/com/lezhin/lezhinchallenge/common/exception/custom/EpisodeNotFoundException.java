package com.lezhin.lezhinchallenge.common.exception.custom;

import com.lezhin.lezhinchallenge.common.exception.ErrorCode;

/**
 * 에피소드를 찾을 수 없을 때 발생하는 예외
 */
public class EpisodeNotFoundException extends EntityNotFoundException {

    public EpisodeNotFoundException() {
        super(ErrorCode.EPISODE_NOT_FOUND);
    }

    public EpisodeNotFoundException(String message) {
        super(ErrorCode.EPISODE_NOT_FOUND, message);
    }
}
