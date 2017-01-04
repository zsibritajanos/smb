package mixcloud;

import mixcloud.auth.Auth;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.HttpUtil;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by zsjanos on 2016.11.24..
 * <p>
 * Wrapper class to https://www.mixcloud.com/developers/
 */
public class MixCloud {

    /**
     * queries
     */
    private static final String USER_QUERY = "https://api.mixcloud.com/";
    private static final String CLOUDCAST_QUERY = "https://api.mixcloud.com/search/?type=cloudcast&q=";
    private static final String NEW_QUERY = "https://api.mixcloud.com/new/?limit=100";
    private static final String[] MY_GENRES = new String[]{"tech house"};


    /**
     * query keys
     */
    private static final String JSON_KEY_NAME = "name";
    private static final String JSON_KEY_CITY = "city";

    /**
     * delay
     */

    private static final int WAIT_FAV_MIN = 15;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * @param username
     * @return
     */
    private static JSONObject getUserInfo(String username) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(HttpUtil.request(USER_QUERY + username));
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * @param username
     * @return
     */
    private static String getName(String username) {

        JSONObject userInfo = getUserInfo(username);

        String userName = null;

        try {
            userName = userInfo.getString(JSON_KEY_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userName;
    }

    private static Set<String> getTags(JSONObject cast) {
        Set<String> ret = new HashSet<>();

        try {
            JSONArray tags = cast.getJSONArray("tags");

            for (int i = 0; i < tags.length(); i++) {
                ret.add(tags.getJSONObject(i).getString("name").toLowerCase());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static String getCity(String username) {

        JSONObject userInfo = getUserInfo(username);

        String userName = null;

        try {
            userName = userInfo.getString(JSON_KEY_CITY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userName;
    }

    /**
     * @param tag
     */
    private static JSONObject searchCloudcast(String tag) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(HttpUtil.request(CLOUDCAST_QUERY + tag));
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * @param date dd/MM/yyyy
     * @return
     */
    private static Timestamp getTimeStamp(String date) {

        Timestamp timestamp = null;
        try {
            timestamp = new Timestamp(DATE_FORMAT.parse(date).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    /**
     * @return
     */
    private static JSONObject searchNew() {


        JSONObject jsonObject = null;
        try {

            jsonObject = new JSONObject(HttpUtil.request(NEW_QUERY));

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static int getUserFollowers(String userKey) {
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

    private static int getUserFollowing(String userKey) {
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

    private static String getUserCountry(String userKey) {
        String prefix = "https://api.mixcloud.com";
        String suffix = "?metadata=1";

        String request = prefix + userKey + suffix;
        String country = null;
        try {
            JSONObject jsonObject = new JSONObject(HttpUtil.request(request));
            if (jsonObject.has("country")) {
                country = jsonObject.getString("country");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return country;
    }

    /**
     * @param tags
     * @return
     */
    private static boolean isMyGenre(Set<String> tags) {
        for (String genre : Auth.getTags()) {
            if (tags.contains(genre)) {
                return true;
            }
        }
        return false;
    }

    private static void likeNews() {

        // init result
        JSONObject result = null;

        try {
            result = new JSONObject(HttpUtil.request(NEW_QUERY));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result != null) {
            while (result.has("paging")) {
                try {
                    JSONArray data = result.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject cast = data.getJSONObject(i);

                        Set<String> tags = getTags(cast);

                        if (isMyGenre(tags)) {
                            // mix key
                            String key = cast.getString("key");
                            // user key
                            String userKey = data.getJSONObject(i).getJSONObject("user").getString("key");

                            int follower = getUserFollowers(userKey);
                            int following = getUserFollowing(userKey);

                            if (10 < follower & follower < 2000) {
                                if (10 < following & following < 2000) {

                                    //String country = getUserCountry(userKey);
                                    //if (country != null && country.equals("Hungary")) {
                                        System.out.println(key);
                                        System.out.println(userKey);
                                        System.out.println(follower);
                                        System.out.println(following);
                                     //   System.out.println(country);
                                        addFavorite(key);
                                        follow(userKey);
                                    //}
                                }
                            }
                        }
                    }

                    result = new JSONObject(HttpUtil.request(result.getJSONObject("paging").getString("next")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void addFavorite(String key) {
        String prefix = "https://api.mixcloud.com";
        String suffix = "favorite/?access_token=" + Auth.getAccessToken();

        String request = prefix + key + suffix;
        String response = HttpUtil.postRequest(request);
        System.out.println(response);

        try {
            TimeUnit.MINUTES.sleep(WAIT_FAV_MIN);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void follow(String userKey) {
        String prefix = "https://api.mixcloud.com";
        String suffix = "follow/?access_token=" + Auth.getAccessToken();
        String request = prefix + userKey + suffix;
        String response = HttpUtil.postRequest(request);

        System.out.println(response);

        try {
            TimeUnit.MINUTES.sleep(WAIT_FAV_MIN);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

        likeNews();
    }
}




