package chengkai.suixingpay.com.dbhelper;

import db.BaseDao;

/**
 * Created by chengkai on 2017/2/7.
 */

public class UserDao extends BaseDao<User> {

    @Override
    protected String createTable() {
        return "create table if not exists tb_user(tb_name varchar(20),tb_password varchar(20))";
    }
}
