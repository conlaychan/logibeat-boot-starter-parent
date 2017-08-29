package com.logibeat.cloud.boot.mybatis;

import com.logibeat.cloud.common.model.BaseEntity;
import com.logibeat.cloud.common.model.EntityCriteria;
import com.logibeat.cloud.common.model.Paging;
import com.logibeat.cloud.common.utils.Params;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class MybatisDao<T extends BaseEntity> {

    @Autowired
    protected SqlSessionTemplate sqlSession;

    protected static final String CREATE = "create";        //添加
    protected static final String CREATES = "creates";      //批量添加
    protected static final String DELETE = "delete";        //删除
    protected static final String DELETES = "deletes";      //批量删除
    protected static final String UPDATE = "update";        //更新
    protected static final String FIND_BY_ID = "findById";  //单个主键查询对象
    protected static final String FIND_BY_IDS = "findByIds";//主键列表查询对象列表
    protected static final String LIST = "list";            //列表条件查询
    protected static final String COUNT = "count";          //计数
    protected static final String PAGING = "paging";        //分页查询

    /**
     * Namespace should be simple className
     */
    private final String nameSpace;
    private final Class<T> clazz;


    public MybatisDao() {
        if (getClass().getGenericSuperclass() instanceof ParameterizedType) {
            clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        } else {
            //解决cglib实现aop时转换异常
            clazz = (Class<T>) ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        nameSpace = clazz.getSimpleName();
    }

    /**
     * 添加对象
     *
     * @param t 范型对象
     * @return 增加记录数
     */
    public int create(T t) {
        Date now = new Date();
        t.setUpdateAt(now);
        t.setCreateAt(now);
        return sqlSession.insert(sqlId(CREATE), t);
    }

    /**
     * 批量添加对象
     *
     * @param ts 范型对象
     * @return 增加记录数
     */
    public int creates(Collection<T> ts) {
        if (ts == null || ts.isEmpty()) {
            return 0;
        }
        Date now = new Date();
        for (T t : ts) {
            t.setUpdateAt(now);
            t.setCreateAt(now);
        }
        return sqlSession.insert(sqlId(CREATES), ts);
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return 删除记录数
     */
    public int delete(Long id) {
        int res = sqlSession.delete(sqlId(DELETE), id);
        if (res != 1) {
            log.warn("Expected to delete 1 row but actually {} deleted, class = {}, id = {}", res, clazz.getSimpleName(), id);
        }
        return res;
    }

    /**
     * 批量删除
     *
     * @param ids 主键列表
     * @return 删除记录数
     */
    public int deletes(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        int res = sqlSession.delete(sqlId(DELETES), ids);
        if (res != ids.size()) {
            log.warn("Expected to delete {} row(s) but actually {} deleted, class = {}, ids = {}", ids.size(), res, clazz.getSimpleName(), ids);
        }
        return res;
    }

    /**
     * 更新对象
     *
     * @param t 范型对象
     * @return 更新记录数
     */
    public int update(T t) {
        t.setUpdateAt(new Date());
        return sqlSession.update(sqlId(UPDATE), t);
    }

    /**
     * 查询单个对象
     *
     * @param id 主键
     * @return 对象
     */
    public T findById(Integer id) {
        return findById(Long.valueOf(id));
    }

    /**
     * 查询单个对象
     *
     * @param id 主键
     * @return 对象
     */
    public T findById(Long id) {
        return sqlSession.selectOne(sqlId(FIND_BY_ID), id);
    }

    /**
     * 查询对象列表
     *
     * @param ids 主键列表
     * @return 对象列表
     */
    public List<T> findByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return sqlSession.selectList(sqlId(FIND_BY_IDS), ids);
    }

    /**
     * 查询所有对象列表
     *
     * @return 所有对象列表
     */
    public List<T> listAll() {
        return list(null);
    }

    /**
     * 查询对象列表
     *
     * @param criteria 筛选对象
     * @return 查询到的对象列表
     */
    public List<T> list(EntityCriteria criteria) {
        return sqlSession.selectList(sqlId(LIST), criteria);
    }

    /**
     * 统计数量
     * @param criteria 筛选对象
     * @return 数量
     */
    public long count(EntityCriteria criteria){
        return sqlSession.selectOne(sqlId(COUNT), criteria);
    }

    /**
     * 查询分页对象
     *
     * @param pageNo 第几页
     * @param pageSize 每页数量
     * @return 查询到的分页对象
     */
    public Paging<T> paging(Integer pageNo, Integer pageSize) {
        return paging(pageNo, pageSize, null);
    }

    /**
     * 查询分页对象
     *
     * @param pageNo 第几页
     * @param pageSize 每页数量
     * @param criteria 即查询条件
     * @return 查询到的分页对象
     */
    public Paging<T> paging(Integer pageNo, Integer pageSize, EntityCriteria criteria) {
        long total = this.count(criteria);
        if (total <= 0) {
            return Paging.empty();
        }

        Object params;
        if (criteria == null) {
            criteria = new EntityCriteria();
            criteria.buildOffsetLimit(pageNo, pageSize);
            params = Params.objToMap(criteria);
        } else {
            criteria.buildOffsetLimit(pageNo, pageSize);
            params = criteria;
        }
        List<T> datas = sqlSession.selectList(sqlId(PAGING), params);
        return new Paging<>(total, datas);
    }

    /**
     * sql语句的id
     *
     * @param id sql id
     * @return "nameSpace.id"
     */
    protected String sqlId(String id) {
        return nameSpace + "." + id;
    }

    protected SqlSessionTemplate getSqlSession() {
        return sqlSession;
    }
}
