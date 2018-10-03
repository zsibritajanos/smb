package youtube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by zsjanos on 2017.01.04..
 */
public class YouTube {


    // https://www.youtube.com/user/SpinninRec
    // https://developers.google.com/youtube/v3/docs/channels/list
    // https://developers.google.com/youtube/registering_an_application

    private static final String API_KEY = "AIzaSyDtGzKyuAM75kMGIGv2xHMJ83S2iHXoRzk";
    private static int wait_sec = 60;

    private static Writer logFile;

    static {
        String logFileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm'.txt'").format(new Date());

        try {
            logFile = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(logFileName), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static String curl(String request) {
        URL url;
        BufferedReader reader;

        String ret = "";

        try {
            url = new URL(request);
            reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            for (String line; (line = reader.readLine()) != null; ) {
                ret += line + "\n";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * @param channelId
     * @return
     */
    private static JSONObject getPage(String channelId) {
        return getPage(channelId, null);
    }

    private static JSONObject getPage(String channelId, String pageToken) {
        StringBuffer query = new StringBuffer("https://www.googleapis.com/youtube/v3/search?key=");
        query.append(API_KEY);

        if (pageToken != null) {
            query.append("&pageToken=" + pageToken);
        }

        query.append("&channelId=" + channelId);
        query.append("&part=id,snippet&order=date&maxResults=50");

        String response = curl(query.toString());


        // init result
        JSONObject result = null;

        try {
            result = new JSONObject(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static JSONObject observeVideo(String videoId) {

        StringBuffer query = new StringBuffer("https://www.googleapis.com/youtube/v3/videos?key=");
        query.append(API_KEY);
        query.append("&id=" + videoId);
        query.append("&part=snippet,statistics");

        String response = curl(query.toString());
        // init result
        JSONObject result = null;

        try {
            result = new JSONObject(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String parseVideo(JSONObject video) {
        String title = null;
        String viewCount = null;
        String published = null;
        try {
            title = ((JSONObject) ((JSONObject) video.getJSONArray("items").get(0)).get("snippet")).getString("title");

            System.out.println(title);

            viewCount = ((JSONObject) ((JSONObject) video.getJSONArray("items").get(0)).get("statistics")).getString("viewCount");
            published = ((JSONObject) ((JSONObject) video.getJSONArray("items").get(0)).get("snippet")).getString("publishedAt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return title + "\t" + viewCount + "\t" + published;
    }

    private static void parsePage(JSONObject result) {
        try {
            JSONArray data = result.getJSONArray("items");
            System.out.println(data.length());
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                if (((JSONObject) item.get("id")).has("videoId")) {
                    String videoId = ((JSONObject) item.get("id")).getString("videoId");
                    JSONObject video = observeVideo(videoId);
                    try {
                        logFile.write(parseVideo(video) + "\n");
                        logFile.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void observeChannel(String channelId) {

        JSONObject result = getPage(channelId);
        parsePage(result);
        try {
            while (result.has("nextPageToken")) {
                try {
                    TimeUnit.SECONDS.sleep(wait_sec);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String nextPageToken = result.getString("nextPageToken");
                result = getPage(channelId, nextPageToken);
                parsePage(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        if (args.length == 1) {
            wait_sec = Integer.parseInt(args[0]);
        }
        System.out.println(wait_sec);

<<<<<<< HEAD
        //https://www.youtube.com/channel/UCOsYFfaNxTqxKFux98XU-EA
        observeChannel("UCWxjcBec-T-ndmw4xXGZkFw");
=======
        //https://www.youtube.com/channel/UCXvSeBDvzmPO05k-0RyB34w


        observeChannel("UCfjaVIei78Nm_kMj6lpVkwQ");
>>>>>>> 8cfbefa96f5213ae297fa6a42accdb1ed8db1808

        try {
            logFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
