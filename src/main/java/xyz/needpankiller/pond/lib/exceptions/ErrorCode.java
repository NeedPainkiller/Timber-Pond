package xyz.needpankiller.pond.lib.exceptions;

import xyz.needpankiller.pond.lib.http.HttpStatus;

public interface ErrorCode {
    String getMessage();

    HttpStatus getStatus();

    String getCode();


}
