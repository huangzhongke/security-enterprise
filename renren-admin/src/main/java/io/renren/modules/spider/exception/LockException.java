package io.renren.modules.spider.exception;

/**
 * @author kee
 * @version 1.0
 * @date 2022/4/13 16:08
 */
public class LockException extends RuntimeException{

    public LockException(String message) {
        super(message);
    }
}
