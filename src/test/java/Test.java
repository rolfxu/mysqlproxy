import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;

import java.util.concurrent.CountDownLatch;

public class Test {
    public static void main(String[] args) throws Exception {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(3306)
                .setHost("localhost")
                .setDatabase("es")
                .setUser("root")
                .setPassword("123456");

// Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(1);

        System.out.println(poolOptions.toJson());
// Create the client pool
        MySQLPool client = MySQLPool.pool(connectOptions, poolOptions);

// A simple query
        CountDownLatch cdl = new CountDownLatch(1);

        for(int i=0;i<1;i++) {
            if(i==5){
                Thread.sleep(10000);
            }
            client
                    .query("SELECT sleep(500)")
                    .execute(ar -> {
                        cdl.countDown();
                        if (ar.succeeded()) {
                            RowSet<Row> result = ar.result();
//                            System.out.println("Got " + result.size() + " rows ");
                        } else {
                            System.out.println("Failure: " + ar.cause().getMessage());
                        }
                    });

        }
        System.out.println("dddd");
        client.getConnection(ar->{
            System.out.println("cccccccccccccccccc");
            SqlConnection conn = ar.result();
            conn.query("select sleep(10)").execute(ar1->{

                if(ar1.succeeded()) {
                    System.out.println("release");
                }
                cdl.countDown();
            });
        });

        cdl.await();
        client.close();
        System.out.println("end");
    }
}
