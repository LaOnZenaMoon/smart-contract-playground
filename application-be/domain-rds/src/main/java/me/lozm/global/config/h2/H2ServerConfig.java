package me.lozm.global.config.h2;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@Profile({"local"})
public class H2ServerConfig {

    @Value("${spring.h2.port}")
    private String h2ServerPort;


    @Bean
    public Server h2TcpServer() throws SQLException {
        return Server.createTcpServer()
                .start();
    }

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() throws SQLException {
        Server start = Server.createTcpServer("-tcp",
                "-tcpPort",
                h2ServerPort,
                "-tcpAllowOthers",
                "-ifNotExists"
        ).start();
        return new com.zaxxer.hikari.HikariDataSource();
    }

}
