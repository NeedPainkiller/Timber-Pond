package xyz.needpankiller.timber.pond.lib.exceptions;

import xyz.needpankiller.timber.pond.lib.http.HttpStatus;

public interface ErrorCode {
    String getMessage();

    HttpStatus getStatus();

    String getCode();


}
