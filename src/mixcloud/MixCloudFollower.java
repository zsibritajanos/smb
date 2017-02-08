package mixcloud;

import mixcloud.auth.Auth;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.HttpUtil;

import java.util.concurrent.TimeUnit;

/**
 * Created by zsjanos on 2017.02.08..
 */
public class MixCloudFollower {


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    private static final String QUERY = "https://api.mixcloud.com/";

    /**
     * @param userKey
     * @return
     */
    private static boolean follow(String userKey) {
        String prefix = "https://api.mixcloud.com";
        String suffix = "follow/?access_token=" + Auth.getAccessToken();
        String request = prefix + userKey + suffix;

        JSONObject response = null;
        try {
            response = new JSONObject(HttpUtil.postRequest(request));
            if (response.getJSONObject("result").getBoolean("success")) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * @param user
     */
    private static void getUsersFollowers(String user) {
        JSONObject result = null;

        try {
            result = new JSONObject(HttpUtil.request(QUERY + user + "/followers/"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (result != null) {
            while (result.has("paging")) {
                try {
                    JSONArray data = result.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        String name = data.getJSONObject(i).getString("name");
                        String userName = data.getJSONObject(i).getString("username");
                        String userKey = data.getJSONObject(i).getString("key");

                        boolean response = follow(userKey);
                        System.out.print(name + "/" + userName + "\t");

                        if (response) {
                            System.out.println(ANSI_GREEN + "OK" + ANSI_RESET);
                        } else {
                            System.out.println(ANSI_RED + "ERROR" + ANSI_RESET);
                        }

                        try {
                            TimeUnit.MINUTES.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    result = new JSONObject(HttpUtil.request(result.getJSONObject("paging").getString("next")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            args = new String[]{"./resources/corvin.properties"};
        }

        Auth.init(args[0]);

        //System.out.println(Auth.getAccessToken());

        getUsersFollowers("djgabrielb2");
    }

}
