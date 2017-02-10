package db;

import java.util.List;

/**
 * Created by chengkai on 2017/2/7.
 */

public interface IBaseDao<T> {

    long insert(T entity);

    int delete(T where);

    int updata(T entity, T where);

    List<T> query(T where);

    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);

}
