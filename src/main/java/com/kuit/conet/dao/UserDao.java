package com.kuit.conet.dao;

import com.kuit.conet.domain.Platform;
import com.kuit.conet.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class UserDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Optional<Long> findByPlatformAndPlatformId(Platform platform, String platformId) {
        String sql = "select user_id from user where platform=:platform and platform_id=:platform_id";
        Map<String, String> param = Map.of(
                "platform", platform.getPlatform(),
                "platform_id", platformId);

        RowMapper<Long> mapper = new SingleColumnRowMapper<>(Long.class);

        List<Long> userIdList = jdbcTemplate.query(sql, param, mapper);
        return userIdList.isEmpty() ? Optional.empty() : Optional.of(userIdList.get(0));
    }

    public Optional<User> findById(Long userId) {
        String sql = "select * from user where user_id=:user_id";
        Map<String, Long> param = Map.of("user_id", userId);

        RowMapper<User> mapper = new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setServiceTerm(rs.getBoolean("service_term"));
                user.setOptionTerm(rs.getBoolean("option_term"));
                String platform = rs.getString("platform");
                user.setPlatform(Platform.valueOf(platform));
                user.setPlatformId(rs.getString("platform_id"));
                return user;
            }
        };
        User user = jdbcTemplate.queryForObject(sql, param, mapper);
        Optional<User> returnUser = Optional.ofNullable(user);

        return returnUser;
    }

    public Optional<User> save(User oauthUser) {
        // 회원가입 -> insert 한 후, 넣은 애 반환
        String sql = "insert into user (email, platform, platform_id) values (:email, :platform, :platform_id)";
        Map<String, String> param = Map.of("email", oauthUser.getEmail(),
                "platform", oauthUser.getPlatform().toString(),
                "platform_id", oauthUser.getPlatformId());

        jdbcTemplate.update(sql, param);

        String returnSql = "select * from user where platform=:platform and platform_id=:platform_id";
        Map<String, String> returnParam = Map.of(
                "platform", oauthUser.getPlatform().toString(),
                "platform_id", oauthUser.getPlatformId());

        RowMapper<User> returnMapper = new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setServiceTerm(rs.getBoolean("service_term"));
                user.setOptionTerm(rs.getBoolean("option_term"));
                String platform = rs.getString("platform");
                user.setPlatform(Platform.valueOf(platform));
                user.setPlatformId(rs.getString("platform_id"));
                return user;
            }
        };

        User user = jdbcTemplate.queryForObject(returnSql, returnParam, returnMapper);

        return Optional.ofNullable(user);
    }

    public Optional<User> agreeTermAndPutName(String name, Boolean optionTerm, Long userId) {
        String sql = "update user set name=:name, service_term=1, option_term=:option_term where user_id=:user_id";
        Map<String, Object> param = Map.of(
                "name", name,
                "option_term", optionTerm,
                "user_id", userId);

        jdbcTemplate.update(sql, param);

        String returnSql = "select * from user where user_id=:user_id";
        Map<String, Object> returnParam = Map.of("user_id", userId);

        RowMapper<User> returnMapper = new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setServiceTerm(rs.getBoolean("service_term"));
                user.setOptionTerm(rs.getBoolean("option_term"));
                String platform = rs.getString("platform");
                user.setPlatform(Platform.valueOf(platform));
                user.setPlatformId(rs.getString("option_term"));
                return user;
            }
        };

        User user = jdbcTemplate.queryForObject(returnSql, returnParam, returnMapper);

        return Optional.ofNullable(user);
    }


    public void deleteUser(Long userId) {
        // user의 platform, platformId 초기화
        String sql = "update user set platform='', platform_id='', service_term=0 where user_id=:user_id";
        Map<String, Object> param = Map.of(
                "user_id", userId);

        jdbcTemplate.update(sql, param);

        // user가 참여한 모든 모임(team) 나가기
        sql = "update team_member set status=0 where user_id=:user_id";
        jdbcTemplate.update(sql, param);
    }
}
