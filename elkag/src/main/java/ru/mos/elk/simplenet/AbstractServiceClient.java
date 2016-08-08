package ru.mos.elk.simplenet;

import android.util.Log;

import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public abstract class AbstractServiceClient {
    private static final int CONNECTION_TIMEOUT = 60000;
    private static final int SOCKET_TIMEOUT = 60000;

    protected String content;
    private int errorCode = HttpURLConnection.HTTP_BAD_REQUEST;

    protected String realCommunicate(URLMethod method, String urlString, String requestBody) {
        Log.i("SERVICE", "REQUEST: " + urlString);
        StringBuilder sBuilder = new StringBuilder();
        HttpURLConnection urlConnection = null;
        URL url;
        BufferedReader bReader = null;
        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            configConnection(method, urlConnection, requestBody.length());
            if (method == URLMethod.POST)
                attachBody(urlConnection, requestBody);
            errorCode = urlConnection.getResponseCode();
            bReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), HTTP.UTF_8));

            String line; // TODO optimize reading using nio or byte reading
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bReader != null)
                try {
                    bReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return sBuilder.toString();
    }

    private void attachBody(HttpURLConnection urlConnection, String requestBody) throws IOException {
        Log.i("SERVICE", "REQUEST BODY:" + requestBody);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
        out.write(requestBody);
        out.flush();
        out.close();
    }

    protected void configConnection(URLMethod method, HttpURLConnection urlConnection, int length) throws ProtocolException {
        urlConnection.setDoOutput(true); //setting request type "POST"
        urlConnection.setDoInput(true);
        urlConnection.setRequestMethod(method.getMethod());
        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        urlConnection.setReadTimeout(SOCKET_TIMEOUT);
        urlConnection.setRequestProperty("Content-Language", "ru-RU");
        urlConnection.setRequestProperty("Accept-Charset", HTTP.UTF_8);
        urlConnection.setRequestProperty("Accept-Encoding", HTTP.IDENTITY_CODING);
        urlConnection.setRequestProperty(HTTP.CONTENT_TYPE, getContentType());
    }

    protected abstract String getContentType();

    public String getResponse() {
        return content;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
