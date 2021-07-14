package com.github.community.actuator;

import com.github.community.util.MyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

// 自定义 Actuator Endpoint
@Component
@Slf4j
@Endpoint(id = "database")
public class DatabaseEndpoint {

    @Autowired
    private DataSource dataSource;

    // 端点只能通过 get 请求访问
    @ReadOperation
    public String checkConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return MyUtil.getJSONString(0, "获取数据库连接成功");
        } catch (SQLException e) {
            log.error("获取连接失败" + e.getMessage());
            return MyUtil.getJSONString(1, "获取数据库连接失败");
        }
    }
}
