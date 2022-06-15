package io.renren.modules.spider.exception;

/**
 * @author kee
 * @version 1.0
 * @date 2022/4/13 16:25
 */
public class TokenDisabledException extends RuntimeException{
    public TokenDisabledException(String message) {
        super(message);
    }
}
