package ru.mos.polls.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Map;

/**
 * Thread for executing request
 */
public class Request extends Thread {
    private static final String DEBUG_REQUEST = "SimpleRequestBuilder";
    /**
     * listener for validating response
     */
    private OnValidateResponseListener onValidateResponseListener;
    /**
     * listener for processing result response
     */
    private ResponseListener responseListener;
    private String url;
    /**
     * Enum for request type, it is contains only POST and GET values
     */
    private RequestType requestType;
    private String envelope;
    private Map<String, String> headers;
    /**
     * set true, if you want to see result of request in logcat
     */
    private boolean isDebug;

    public Request() {
    }

    @Override
    public void run() {
        onPostExecute(doInBackground());
    }

    /**
     * Execute request in background thread
     *
     * @return
     */
    protected HttpUtils.HttpResult doInBackground() {
        HttpUtils.HttpResult result = null;
        if (RequestType.POST == requestType) {
            result = HttpUtils.post(url, envelope, headers);
        } else if (RequestType.GET == requestType) {
            result = HttpUtils.get(url, headers);
        }
        return result;
    }

    /**
     * Process response, call in UI thread
     *
     * @param httpResult
     */
    protected void onPostExecute(final HttpUtils.HttpResult httpResult) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (isDebug) {
                    toLog(httpResult);
                }
                if (onValidateResponseListener != null) {
                    if (onValidateResponseListener.isResponseExecuted(httpResult)) {
                        responseListener.onSuccess(httpResult);
                    } else {
                        responseListener.onError(httpResult);
                    }
                }
            }
        });
    }

    /**
     * Print param of request to log
     *
     * @param httpResult - result of request
     */
    private void toLog(HttpUtils.HttpResult httpResult) {
        Log.d(DEBUG_REQUEST, "request url: " + url);
        Log.d(DEBUG_REQUEST, "request envelope: " + envelope);
        Log.d(DEBUG_REQUEST, "request type: " + requestType);
        if (httpResult != null) {
            Log.d(DEBUG_REQUEST, "response code: " + httpResult.getCode());
            Log.d(DEBUG_REQUEST, "response envelope: " + httpResult.getEnvelope());
        } else {
            Log.d(DEBUG_REQUEST, "response null");
        }
    }

    /**
     * Class builder for lazy initialization newInstance of Request
     */
    public static class Builder {
        private String url;
        private RequestType requestType = RequestType.GET;
        private ResponseListener responseListener = ResponseListener.STUB;
        private OnValidateResponseListener validateResponseListener = OnValidateResponseListener.STUB;
        private String envelope;
        private Map<String, String> headers;
        private boolean isDebug = true;

        public Builder() {
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setRequestType(RequestType requestType) {
            if (requestType == null) {
                this.requestType = RequestType.GET;
            } else {
                this.requestType = requestType;
            }
            return this;
        }

        public Builder setResponseListener(ResponseListener responseListener) {
            if (responseListener == null) {
                this.responseListener = ResponseListener.STUB;
            } else {
                this.responseListener = responseListener;
            }
            return this;
        }

        public Builder setOnValidateResponseListener(OnValidateResponseListener validateResponseListener) {
            if (validateResponseListener == null) {
                this.validateResponseListener = OnValidateResponseListener.STUB;
            } else {
                this.validateResponseListener = validateResponseListener;
            }
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder setEnvelope(String envelope) {
            this.envelope = envelope;
            return this;
        }

        public Builder setDebug(boolean isDebug) {
            this.isDebug = isDebug;
            return this;
        }

        /**
         * Call to create newInstance of request, set params to newInstance and execute
         */
        public void build() {
            Request request = new Request();

            request.requestType = requestType;
            request.url = url;
            request.envelope = envelope;
            request.headers = headers;

            request.responseListener = responseListener;
            request.onValidateResponseListener = validateResponseListener;

            request.isDebug = isDebug;

            request.start();
        }
    }

    /**
     * Interface for process results of request
     */
    public interface ResponseListener {
        ResponseListener STUB = new ResponseListener() {
            @Override
            public void onSuccess(HttpUtils.HttpResult httpResult) {
            }

            @Override
            public void onError(HttpUtils.HttpResult httpResult) {
            }
        };

        void onSuccess(HttpUtils.HttpResult httpResult);

        void onError(HttpUtils.HttpResult httpResult);
    }

    /**
     * Interface for validation response
     * It has STUB newInstance, witch set true if response HttpCode == 200
     */
    public interface OnValidateResponseListener {
        OnValidateResponseListener STUB = new OnValidateResponseListener() {
            @Override
            public boolean isResponseExecuted(HttpUtils.HttpResult httpResult) {
                return httpResult != null && httpResult.getCode() == HttpUtils.HttpResult.OK;
            }
        };

        boolean isResponseExecuted(HttpUtils.HttpResult httpResult);
    }

    /**
     * Type of request, contains only two http methods
     */
    public enum RequestType {
        POST,
        GET
    }
}
