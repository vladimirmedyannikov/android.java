package ru.mos.polls.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Class for executing http post or get request
 */
public class HttpUtils {
    public static final int CONNECTION_TIMEOUT = 15000;
    public static final int TIMEOUT = 15000;

    /**
     * Call to execute post request without headers for request
     *
     * @param method   - url
     * @param envelope - envelope for post request
     * @return - HttpResult instance for response
     */
    public static HttpResult post(String method, String envelope) {
        return post(method, envelope, null);
    }

    /**
     * Call to execute post request
     *
     * @param method   - url
     * @param envelope - string envelope for post request
     * @param headers  - headrs for request
     * @return - HttpResult instance for response
     */
    public static HttpResult post(String method, String envelope, Map<String, String> headers) {
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        /**
         * set params t request
         */
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT);
        HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), true);
        /**
         * set url
         */
        HttpPost httpPost = new HttpPost(method);
        /**
         * set headers
         */
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet())
                httpPost.setHeader(header.getKey(), header.getValue());
        }
        /**
         * execute request and generate HttpResult instance
         */
        final HttpResult resultHttpResult = new HttpResult();
        try {
            /**
             * set envelope for post request
             */
            envelope = new String(envelope.getBytes("UTF-8"), "UTF-8");
            HttpEntity entity = new StringEntity(envelope, HTTP.UTF_8);
            httpPost.setEntity(entity);
            /**
             * execute request
             */
            String resultEnvelope = httpClient.execute(httpPost,
                    new DefaultResponseHandler(resultHttpResult));
            resultHttpResult.setEnvelope(resultEnvelope);
        } catch (ClientProtocolException ignored) {
        } catch (IOException ignored) {
        }

        return resultHttpResult;
    }

    /**
     * Call to execute get request without headers
     *
     * @param method - url
     * @return - HttpResult instance for response
     */
    public static HttpResult get(String method) {
        return get(method, null);
    }

    /**
     * Call to execute get request
     *
     * @param method  - url
     * @param headers - headers
     * @return - HttpResult instance for response
     */
    public static HttpResult get(String method, Map<String, String> headers) {
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        /**
         * set params to request
         */
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT);
        HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), true);
        /**
         * set url
         */
        HttpGet httpGet = new HttpGet(method);
        /**
         * set headers to request
         */
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet())
                httpGet.setHeader(header.getKey(), header.getValue());
        }
        /**
         * execute request and generate HttpResult instance
         */
        final HttpResult resultHttpResult = new HttpResult();
        try {
            String envelope = httpClient.execute(httpGet,
                    new DefaultResponseHandler(resultHttpResult));
            resultHttpResult.setEnvelope(envelope);
        } catch (ClientProtocolException ignored) {
        } catch (IOException ignored) {
        }

        return resultHttpResult;
    }

    /**
     * Call for checking internet connection
     *
     * @param context - context from activity
     * @return - true, if internet is connecting
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Default response handler need for execute HttpClient
     * Set string response to httpResult
     */
    private static class DefaultResponseHandler implements ResponseHandler<String> {
        private final HttpResult httpResult;

        DefaultResponseHandler(HttpResult httpResult) {
            this.httpResult = httpResult;
        }

        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            HttpEntity entity = response.getEntity();
            httpResult.setMessage(response.getStatusLine().getReasonPhrase());
            httpResult.setCode(response.getStatusLine().getStatusCode());
            StringBuilder out = new StringBuilder();
            byte[] data = EntityUtils.toByteArray(entity);
            out.append(new String(data, 0, data.length));
            return out.toString();
        }
    }

    /**
     * Data structure for describing http response
     */
    public static class HttpResult {
        public static final int OK = 200;

        private int code;
        private String message;
        private String envelope;

        public int getCode() {
            return code;
        }

        public String getEnvelope() {
            return envelope;
        }

        public String getMessage() {
            return message;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setEnvelope(String envelope) {
            this.envelope = envelope;
        }
    }
}
