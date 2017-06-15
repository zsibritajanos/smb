package mixcloud.util;

import java.io.*;

/**
 * Created by zsibritajanos on 2017. 06. 15..
 */
public class JSONParser {


    private static void parse(String file){

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line;

        try {
            while ((line = reader.readLine()) != null)   {
                System.out.println (line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        parse("/Users/zsibritajanos/IdeaProjects/smb/src/mixcloud/data/corvin.following.txt");
    }
}
