package mixcloud;

import mixcloud.auth.Auth;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.HttpUtil;

import java.io.*;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by zsjanos on 2017.06.13..
 */
public class AccountCleaner {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static final String FOLLOWING_QUERY = "https://api.mixcloud.com/corvin/following/";

    private static void logFollowing(String logFile) {

        int index = 0;

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(logFile), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // init result
        JSONObject result = null;

        try {
            result = new JSONObject(HttpUtil.request(FOLLOWING_QUERY));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result != null) {
            try {
                while (result.has("paging") && result.getJSONObject("paging").has("next")) {
                    try {
                        JSONArray data = result.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject user = data.getJSONObject(i);
                            try {
                                writer.write(user + "\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            index++;

                            if (index % 100 == 0) {
                                System.out.println(index);
                            }
                        }

                        result = new JSONObject(HttpUtil.request(result.getJSONObject("paging").getString("next")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static int getFollowingCount(String userKey) {
        String prefix = "https://api.mixcloud.com";
        String suffix = "?metadata=1";

        String request = prefix + userKey + suffix;
        int following = 0;
        try {
            following = new JSONObject(HttpUtil.request(request)).getInt("following_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return following;
    }

    private static int getFollowerCount(String userKey) {
        String prefix = "https://api.mixcloud.com";
        String suffix = "?metadata=1";

        String request = prefix + userKey + suffix;
        int followers = 0;
        try {
            followers = new JSONObject(HttpUtil.request(request)).getInt("follower_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return followers;
    }

    private static boolean deleteFollowing(String userKey) {
        String prefix = "https://api.mixcloud.com";
        String suffix = "follow/?access_token=" + Auth.getAccessToken();
        String request = prefix + userKey + suffix;

        JSONObject response;
        try {
            response = new JSONObject(HttpUtil.delRequest(request));
            if (response.getJSONObject("result").getBoolean("success")) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }
    }

    private static void clean() {

        JSONObject result = null;

        try {
            result = new JSONObject(HttpUtil.request(FOLLOWING_QUERY));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result != null) {
            try {
                while (result.has("paging") && result.getJSONObject("paging").has("next")) {
                    try {
                        JSONArray data = result.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject user = data.getJSONObject(i);

                            String userKey = user.getString("key");

                            System.out.print(userKey + "\t");

                            int followerCount = getFollowerCount(userKey);
                            int followingCount = getFollowingCount(userKey);

                            if (followerCount > 2000 && 1000 > followingCount) {
                                System.out.println(ANSI_WHITE + followerCount + "\t" + followingCount + ANSI_RESET);
                            } else {
                                boolean response = deleteFollowing(userKey);

                                if (response) {
                                    System.out.println(ANSI_GREEN + "OK" + ANSI_RESET);
                                } else {
                                    System.out.println(ANSI_RED + "ERROR" + ANSI_RESET);
                                }

                                try {
                                    TimeUnit.MINUTES.sleep(2);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        result = new JSONObject(HttpUtil.request(result.getJSONObject("paging").getString("next")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {

        if (args.length < 1) {
            args = new String[]{"./resources/corvin.properties"};
        }

        Auth.init(args[0]);

        System.out.println(args[0]);
        System.out.println(Auth.getAccessToken());
        System.out.println(Auth.getTags());

        // logFollowing("./corvin.following.txt");
        clean();
    }
}

