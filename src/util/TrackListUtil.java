package util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class TrackListUtil {
    public static void main(String[] args){
        File folder = new File("d:/CORVIN_GOOGLE_DRIVE/SZIN50/PL/");
        File[] listOfFiles = folder.listFiles();

        List<String> names = new LinkedList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {

                String name = listOfFiles[i].getName();
                name = name.substring(3);
                name = name.split(" - ")[0];
                name = name.split(" ft ")[0];

                name = name.replace(" x ", " & ");


                String[] ns = name.split(" & ");

                for (String n : ns){
                    if (names.contains(n)){

                    }else{
                        names.add(n);
                    }
                }
            }
        }


        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {

                String name = listOfFiles[i].getName();
                name = name.substring(3);

                String[] split = name.split(" - ");

                System.out.println(split[0] + " - "+ split[1].split("\\(")[0]);
            }
        }


        for (String n : names){
            System.out.print(n + ", ");
        }
    }
}
