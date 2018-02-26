package facebook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.HttpUtil;

import java.io.*;

public class MainTest {


    private static String ACCESS_TOKEN = "EAACEdEose0cBAIR1dy4nrmUTeZAdaoKCsBTX0ZCWuDZCwUtcLim3gNPRUCJgE7RuED0rYT3pqo3lwIENDb9Pz7vDBZBZAOiKvs5hCYAmLlZBgUr9EwUqOVZCAZCzxrjJdodZAohSG5gEe5AT4uPzQOLz3UWhlGXEEvZCk7cxyI1QHGZCsbeun7foZAV0www2T5WGe9YZD";

    private static String getCommentQuery(String id) {
        return "https://graph.facebook.com/v2.11/" + id + "/comments?access_token=" + ACCESS_TOKEN;
    }

    private static String getPostQuery(String domain) {
        return "https://graph.facebook.com/v2.11/" + domain + "/posts?fields=created_time%2Ccaption%2Ccoordinates%2Cshares%2Clink%2Cmessage%2Clikes%7Bid%2Cname%2Cusername%7D&limit=100&access_token=" + ACCESS_TOKEN;
    }

    private static void getPosts(String domain) {


        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("d:/" + domain + ".txt"), "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject result = null;

        try {
            result = new JSONObject(HttpUtil.request(getPostQuery(domain)));

            JSONArray data = result.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                try {

                    String id = data.getJSONObject(i).getString("id");

                    if (data.getJSONObject(i).toString().contains("NAV")) {
                        getComments(id);
                    }

                    writer.write(data.getJSONObject(i) + "\n");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (result != null) {
                try {
                    while (result.has("paging") && result.getJSONObject("paging").has("next")) {
                        result = new JSONObject(HttpUtil.request(result.getJSONObject("paging").getString("next")));
                        data = result.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            try {
                                String id = data.getJSONObject(i).getString("id");

                                if (data.getJSONObject(i).toString().contains("NAV")) {
                                    getComments(id);
                                }

                                writer.write(data.getJSONObject(i) + "\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(result);
    }

    private static void getComments(String id) {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("d:/" + id + ".txt"), "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject result = null;

        try {
            result = new JSONObject(HttpUtil.request(getCommentQuery(id)));

            JSONArray data = result.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                try {
                    writer.write(data.getJSONObject(i) + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (result != null) {
                try {
                    while (result.has("paging") && result.getJSONObject("paging").has("next")) {
                        result = new JSONObject(HttpUtil.request(result.getJSONObject("paging").getString("next")));
                        data = result.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            try {
                                writer.write(data.getJSONObject(i) + "\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(result);
    }

    public static void main(String[] args) {

        String[] domains = {"24ponthu", "444.hu", "hvghu", "indexhu", "OrigoHirek"};

        for (String domain : domains) {
            getPosts(domain);
        }
    }
}
