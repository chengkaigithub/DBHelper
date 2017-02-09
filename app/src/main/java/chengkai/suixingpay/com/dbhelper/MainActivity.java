package chengkai.suixingpay.com.dbhelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import db.DBFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDao = DBFactory.getInstance().getDBHelper(UserDao.class, User.class);

    }

    private List<User> getData(){
        List<User> users = new ArrayList<>();
        Collections.addAll(users,
                new User("林冲", "123456"),
                new User("武松", "654321"),
                new User("李逵", "837561"),
                new User("秦明", "345278"),
                new User("徐宁", "168504"),
                new User("张顺", "278594"),
                new User("燕青", "073572"),
                new User("时迁", "143157"),
                new User("呼延灼", "987456"),
                new User("鲁智深", "087567"));
        return users;
    }

    public void insert(View view) {
        List<User> data = getData();
        for (User user : data) {
            userDao.insert(user);
        }
    }

    public void delete(View view) {
        User where = new User();
        where.setName("徐宁");
        userDao.delete(where);
    }

    public void updata(View view) {
        User user = new User("花荣", "199245");
        User where = new User().setName("张顺");
        userDao.updata(user, where);
    }

    public void query(View view) {
        List<User> queryResult = userDao.query(new User().setName("wangliming"));
        for (User user : queryResult) {
            Log.e(TAG, user.toString());
        }
    }

}
