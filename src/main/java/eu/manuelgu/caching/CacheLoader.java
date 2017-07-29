package eu.manuelgu.caching;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CacheLoader {
    private final LoadingCache<Integer, Integer> cache = CacheBuilder
            .newBuilder()
            // Expire cache after 30 seconds
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(new com.google.common.cache.CacheLoader<Integer, Integer>() {
                @Override
                public Integer load(Integer id) throws Exception {
                    select.setInt(1, id);

                    if (select.execute()) {
                        ResultSet resultSet = select.getResultSet();

                        Integer result = -1;
                        if (resultSet.next()) {
                            result = resultSet.getInt("score");
                        }
                        resultSet.close();
                        return result;
                    }
                    return -1;
                }
            });

    private PreparedStatement select;

    public CacheLoader() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://hostname/database?user=user&password=password"); //TODO
            select = connection.prepareStatement("SELECT SQL_NO_CACHE SUM(`value`) AS `score` FROM `caching` WHERE `user_id` = ?;");

            // Initial insert
            PreparedStatement insert = connection.prepareStatement("INSERT INTO `caching`(`user_id`, `key`, `value`) VALUES (?,?,?);");

            for (int i = 0; i < 10000000; i++) {
                int userId = (int) (Math.random() * 100);
                insert.setInt(1, userId);
                insert.setString(2, "user" + String.valueOf(userId));
                insert.setInt(3, 2);
                insert.executeUpdate();
            }

            insert.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getScore(Integer id) {
        try {
            return this.cache.get(id);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
