package ru.mos.elk.netframework.request;

/**
 * @author Александр Свиридов
 *
 */
public interface ResponseErrorCode {
    public final static int WRONG_RESPONSE = -1;
    public final static int OK = 0;
    public final static int UNAUTHORIZED = 401;
    public static final int SESSION_DOWN = 403;
}
