package com.github.community.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * 该类的作用为：创建 wkhtmltoimage 图片存放的路径，如果没有就创建，如果有则不创建
 */
@Configuration
@Slf4j
public class WkConfig {
    @Value("${wk.image.storage}")
    private String wkImageStorage;


    @PostConstruct
    public void init() {
        File file = new File(wkImageStorage);
        if (!file.exists()) {
            file.mkdir();
            log.info("创建 WK Image 目录：" + wkImageStorage);
        }
    }

}
