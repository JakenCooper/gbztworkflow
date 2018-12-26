package com.gbzt.gbztworkflow.modules.redis.pool;

import com.gbzt.gbztworkflow.consts.AppConst;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisTool {
    private static JedisTool tool = new JedisTool();
    private JedisTool(){
      //  pool = getPool();
    }

    private JedisPool pool = null;
    private JedisPool getPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(3000);
        config.setMaxIdle(50000);
        config.setMaxWaitMillis(10000L);
        config.setTestOnBorrow(true);
        config.setTestOnCreate(true);

        JedisPool pool = null;
        try {
            pool = new JedisPool(config,AppConst.REDIS_SERVER_HOST,Integer.parseInt(AppConst.REDIS_SERVER_PORT),
                    3000);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return pool;
//        return null;
    }

    public static Jedis getJedis(){
        if(tool.pool == null){
            tool.pool = tool.getPool();
        }
        return tool.pool.getResource();
    }

    public static void returnJedis(Jedis jedis){
        tool.pool.returnResource(jedis);
    }
}
