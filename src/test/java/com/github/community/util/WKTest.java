package com.github.community.util;

import java.io.IOException;

public class WKTest {
    public static void main(String[] args) {
        exec();
    }

    public static boolean exec(){
        String command = "/usr/local/bin/wkhtmltoimage --quality 75 https://www.baidu.com /Users/macbook/Desktop/myProject/wkhtmltopdf/image/1.png";
        try {
            Process process = Runtime.getRuntime().exec(command);
            if(process.waitFor() == 0){
                return true;
            }
            return false;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}


