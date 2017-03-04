package db;
import org.junit.Test;
import static org.junit.Assert.*;

public class testDatabase {

    @Test
    public void testLoad() {
        Database db = new Database();
        db.transact("load t1");
        db.transact("create table t2 (x int, y int)");
        db.transact("insert into t2 values 2, 5");
        db.transact("insert into t2 values 8, 3");
        db.transact("insert into t2 values 13, 7");

        String expected = db.transact("print t1");
        String actual = db.transact("print t2");

        assertEquals(expected, actual);
    }


    @Test
    public void testStore() {
        Database db = new Database();
        db.transact("create table t3 (x int, y int)");
        db.transact("insert into t3 values 10, 5");
        db.transact("insert into t3 values 6, 3");
        db.transact("insert into t3 values 11, 7");
        db.transact("store t3");
        db.transact("drop table t3");
        db.transact("load t3");

        db.transact("create table t4 (x int, y int)");
        db.transact("insert into t4 values 10, 5");
        db.transact("insert into t4 values 6, 3");
        db.transact("insert into t4 values 11, 7");

        String actual = db.transact("print t3");
        String expected = db.transact("print t4");

        assertEquals(actual,expected);
    }

    @Test
    public void testSelect() {
        Database db = new Database();
        db.transact("load t3");
        db.transact("load t1");
        db.transact("select x,y from t3,t1 where x > y");
    }


}
