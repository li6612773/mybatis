package com.southwest.mybatis;

import com.southwest.mybatis.entity.User;
import com.southwest.mybatis.service.UserService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@MapperScan("com.southwest.mybatis.mapper")
@SpringBootApplication
@EnableTransactionManagement
public class MybatisApplication {



    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(MybatisApplication.class, args);
        UserService userService = applicationContext.getBean(UserService.class);



        if (userService.fetchUserByEmail("bob@example.com") == null) {
            User bob = userService.register("bob@example.com", "bob123", "Bob");
            System.out.println("Registered ok: " + bob);
        }
        if (userService.fetchUserByEmail("alice@example.com") == null) {
            User alice = userService.register("alice@example.com", "helloalice", "Alice");
            System.out.println("Registered ok: " + alice);
        }
        if (userService.fetchUserByEmail("tom@example.com") == null) {
            User tom = userService.register("tom@example.com", "tomcat", "Alice");
            System.out.println("Registered ok: " + tom);
        }
        // 查询所有用户:
        for (User u : userService.getUsers(1)) {
            System.out.println(u);
        }
        System.out.println("login...");
        User tom = userService.login("tom@example.com", "tomcat");
        System.out.println(tom);
    }


    @Bean
    DataSource createDataSource(
            // JDBC URL:
            @Value("${jdbc.url}") String jdbcUrl,
            // JDBC username:
            @Value("${jdbc.username}") String jdbcUsername,
            // JDBC password:
            @Value("${jdbc.password}") String jdbcPassword) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(jdbcUsername);
        config.setPassword(jdbcPassword);
        config.addDataSourceProperty("autoCommit", "false");
        config.addDataSourceProperty("connectionTimeout", "5");
        config.addDataSourceProperty("idleTimeout", "60");
        return new HikariDataSource(config);
    }

    @Bean
    SqlSessionFactoryBean createSqlSessionFactoryBean(@Autowired DataSource dataSource) {
        var sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean;
    }

    @Bean
    PlatformTransactionManager createTxManager(@Autowired DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
