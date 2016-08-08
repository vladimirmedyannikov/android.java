package ru.mos.polls.social.model;

/**
 * Константы кодов ошибок социальных сетей
 */
public interface Error {
    String POSTING_ERROR = "POSTING_ERROR";

    public interface Twitter {
        /**
         * Ошибка дублирования поста
         * <p/>
         * 403:The request is understood, but it has been refused. An accompanying error message will explain why. This code is used when requests are being denied due to update limits (https://support.twitter.com/articles/15364-about-twitter-limits-update-api-dm-and-following).
         * message - Status is a duplicate.
         * code - 187
         */
        int STATUS_MESSAGE_IS_A_DUPLICATE = 187;
    }

    public interface Facebook {
        /**
         * Ошибка дублирования поста
         * <p/>
         * {HttpStatus: 500, errorCode: 506, errorType: FacebookApiException, errorMessage: Duplicate status message}
         */
        int DUPLICATE_STATUS_MESSAGE = 506;

        /**
         * Одни и те же сообщения в ленте, фб считает это спамом, поэтому возвращает данную ошибку
         * более подробно http://stackoverflow.com/questions/8779379/facebook-graph-api-error-feed-action-request-limit-reached?answertab=active#tab-top
         * <p/>
         * {HttpStatus: 500, errorCode: 341, errorType: FacebookApiException, errorMessage: Feed action request limit reached}
         */
        int REQUEST_LIMIT_REACHED = 341;

        /**
         * Ошибка, связанная с тем, что токен просрочен, в частности из - за смены пароля пользвоателем
         * {HttpStatus: 400, errorCode: 190, errorType: OAuthException, errorMessage: Error validating access token: The session has been invalidated because the user has changed the password.}
         */
        int ERROR_VALIDATING_ACCESS_TOKEN_AFTER_CHANGE_PASSWORD = 190;

        int ERROR_VALIDATING_ACCESS_TOKEN = 2500;

        int UNKNOWN_ERROR = 1;

        /**
         * {HttpStatus: 403, errorCode: 200, errorType: OAuthException, errorMessage: (#200) The user hasn't authorized the application to perform this action}
         */
        int USER_HAS_NOT_AUTHORIZED_THE_APPLICATION = 200;
    }

    public interface Ok {
        /**
         * Сессия истекла
         * <p/>
         * PARAM_SESSION_EXPIRED : Session expired
         */
        int PARAM_SESSION_EXPIRED = -1;

        /**
         * {"error_code":102,"error_msg":"PARAM_SESSION_EXPIRED : Session expired","error_data":null}
         */
        int ERROR_SESSION_EXPIRED = 102;

        /**
         * {"error_data":"publish_to_stream","error_code":10,"error_msg":"PERMISSION_DENIED : User must grant an access to permission 'PUBLISH_TO_STREAM'"}
         */
        int ERROR_PERMISSION_DENIED = 10;

        String OPERATION_WAS_CANCELED_BY_USER = "Operation was cancelled by user";
        String SESSION_IS_EXPIRED = "Session is expired";
    }

    public interface Vk {
        /**
         * "error_code": 5
         * "error_msg": "User authorization failed: invalid session."
         */
        int ERROR_AUTH_FAILED = 5;
    }
}
