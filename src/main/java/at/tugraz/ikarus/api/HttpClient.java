package at.tugraz.ikarus.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpClient {

int timeout;

HttpClient(int timeout) {
    this.timeout = timeout;
}

String get(String url_s) throws IOException {
    System.out.println("GET " + url_s);
    try {
        URL url = new URL(url_s);

        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(timeout);
        con.setReadTimeout(timeout);

        String result = readResponse(con);
        System.out.println("\treturned: " + result);
        return result;
    } catch (MalformedURLException e) {
        e.printStackTrace();
        return null;
    }
}

String delete(String url_s) throws IOException {
    System.out.println("DELETE " + url_s);
    try {
        URL url = new URL(url_s);

        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.setConnectTimeout(timeout);
        con.setReadTimeout(timeout);

        String result = readResponse(con);
        System.out.println("\treturned: " + result);
        return result;
    } catch (MalformedURLException e) {
        e.printStackTrace();
        return null;
    }
}

String post(String url_s, String data) throws IOException {
    System.out.println("\tPOST " + data + " to url " + url_s);
    try {
        URL url = new URL(url_s);

        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setConnectTimeout(timeout);
        con.setReadTimeout(timeout);

        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(data);
        out.flush();
        out.close();

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
