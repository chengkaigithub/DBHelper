package chengkai.suixingpay.com.dbhelper;

import db.annotation.DBField;
import db.annotation.DBTable;

/**
 * Created by chengkai on 2017/2/7.
 */
@DBTable("tb_user")
public class User {

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User() {
    }

    @DBField("tb_name")
    public String name;

    @DBField("tb_password")
    public String password;

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
