package at.tugraz.ikarus.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class HttpClient {

private int timeout;

HttpClient(int timeout) {
    this.timeout = timeout;
}

String get(String url_s) throws IOException {
    return request(url_s, "GET", null);
}

String delete(String url_s) throws IOException {
    return request(url_s, "DELETE", null);
}

String post(String url_s, String data) throws IOException {
    return request(url_s, "POST", data);
}

private String request(String url_s, String method, String data) throws IOException {
    System.out.println(method + ": " + url_s + (data == null ? "" : ": {" + data + "}"));
    try {
        URL url = new URL(url_s);

        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setConnectTimeout(timeout);
        con.setReadTimeout(timeout);

        if (data != null) {
            con.setDoOutput(true);
            final DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(data);
            out.flush();
            out.close();
        }

        String result = readResponse(con);
        System.out.println("\treturned: " + result);
        return result;
    } catch (MalformedURLException e) {
        e.printStackTrace();
        return null;
    }
}

private String readResponse(HttpURLConnection con) throws IOException {
    try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
        String inputLine;
        final StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        String result = content.toString();
        return result.isEmpty() ? null : result;
    }
}
}
