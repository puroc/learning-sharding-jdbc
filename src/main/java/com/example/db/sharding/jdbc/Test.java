package com.example.db.sharding.jdbc;

import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.InlineShardingStrategyConfiguration;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by puroc on 2017/11/15.
 */
public class Test {

    public static void main(String[] args) {
        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();

        // 配置第一个数据源
        BasicDataSource dataSource1 = new BasicDataSource();
        dataSource1.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource1.setUrl("jdbc:mysql://localhost:3306/ds_0");
        dataSource1.setUsername("root");
        dataSource1.setPassword("");
        dataSourceMap.put("ds_0", dataSource1);

        // 配置第二个数据源
        BasicDataSource dataSource2 = new BasicDataSource();
        dataSource2.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource2.setUrl("jdbc:mysql://localhost:3306/ds_1");
        dataSource2.setUsername("root");
        dataSource2.setPassword("");
        dataSourceMap.put("ds_1", dataSource2);

        // 配置Order表规则
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
        orderTableRuleConfig.setLogicTable("t_order");
        orderTableRuleConfig.setActualDataNodes("ds_${0..1}.t_order_${[0, 1]}");

        // 配置分库策略
        orderTableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "ds_${user_id % 2}"));

        // 配置分表策略
        orderTableRuleConfig.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("order_id", "t_order_${order_id % 2}"));

        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);

        // 省略配置order_item表规则...

        // 获取数据源对象
        try {
            DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
