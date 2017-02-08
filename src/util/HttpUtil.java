package util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zsjanos on 2016.11.24..
 */
public class HttpUtil {

    /**
     * @param request
     * @return
     */
    public static String request(String request) {

        StringBuffer response = new StringBuffer();

        try {
            URL url = new URL(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            for (String line; (line = reader.readLine()) != null; ) {
                response.append(line + "\n");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    public static String postRequest(String postUrl) {

        try {

            //  url = new URL("http://localhost:9200/users/user/_search?q=lemma:" + "MSZP" + "&pretty=true");

            //String params = URLEncoder.encode("");

            URL url = new URL(postUrl);

            // System.out.println(url);

            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.connect();

            InputStream is = new BufferedInputStream(httpUrlConnection.getInputStream());
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(is));

            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            String result = stringBuilder.toString();
            is.close();
            responseStreamReader.close();
            httpUrlConnection.disconnect();
            return result;

        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }

    }
}
