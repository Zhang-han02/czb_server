package me.zhengjie.utils;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.czb.weigh.domain.ServerConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * jdbc工具类
 */
@Slf4j
public class JdbcUtil {

    public static Connection obtain(ServerConfig serverConfig) throws Exception {
        String driverClass = "", type = "", dbInfo = "";
        if ("1".equals(serverConfig.getDbType())) {
            //SQL Server
            driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            type = "sqlserver";
            dbInfo = String.format(";databaseName=%s;integratedSecurity=false;", serverConfig.getDbName());

        } else if ("2".equals(serverConfig.getDbType())) {
            //MySQL
            driverClass = "com.mysql.jdbc.Driver";
            type = "mysql";
            dbInfo = String.format("/%s", serverConfig.getDbName());
        }
        Class.forName(driverClass);

        String url = String.format("jdbc:%s://%s:%s%s", type, serverConfig.getDbIp(), serverConfig.getDbPort(), dbInfo);

        Connection connection = DriverManager.getConnection(url, serverConfig.getDbUsername(), serverConfig.getDbPassword());
        return connection;
    }

    public static boolean closeConnection(Connection connection) {
        try {
            connection.close();
            return true;
        } catch (SQLException e) {
            log.error("关闭数据库连接错误：", e);
            return false;
        }
    }
}
