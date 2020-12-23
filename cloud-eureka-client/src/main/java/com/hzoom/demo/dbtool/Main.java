package com.hzoom.demo.dbtool;

public class Main {
    private static final String[] TABLE_NAME_ARRAY = {
            "t_activity_item",
            "t_activity_tab",
            "t_activity_reward_item",
            "t_activity_record", };

    public static void main(String args[]) {
        MySqlServerInfo db1 = new MySqlServerInfo();
        db1.url = "192.168.1.201:3306";
        db1.dbName = "domino";
        db1.setting = "useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=GMT%2b7&allowMultiQueries=true";
        db1.usrName = "dev";
        db1.password = "dev";

        MySqlServerInfo db2 = new MySqlServerInfo();
        db2.url = "58e632b171911.sh.cdb.myqcloud.com:17343";
        db2.dbName = "domino";
        db2.setting = "useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=GMT%2b7&allowMultiQueries=true";
        db2.usrName = "game_test";
        db2.password = "GameTest1402";

        new TableCompareUtil().compare(db1, db2, TABLE_NAME_ARRAY);
    }
}
