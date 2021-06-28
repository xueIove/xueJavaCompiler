package com.xuelove.excel.Refactor;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.xuelove.excel.Refactor.util.App;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

// 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
public class ExcelDemo extends AnalysisEventListener<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDemo.class);
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<Object> list = new ArrayList<Object>();
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private Object student;
    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param
     */
    // 这里是demo，所以随便new一个。实际使用如果到了spring,请使用下面的有参构造函数
    public ExcelDemo() {
        ClassLoader classloader = new App.MyClassLoader();
        Class<?> clazz = null;
        Object o = null;
        try {
            clazz = classloader.loadClass("ChangLong");
            Constructor<?> constructor = clazz.getConstructor();
             o= constructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        student = o;
    }
    /**
     * 这个每一条数据解析都会来调用
     * <p>
     * <p>
     * one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     */
    @SneakyThrows
    @Override
    public void invoke(Object student, AnalysisContext analysisContext) {

        LOGGER.info("解析到一条数据:{}", JSON.toJSONString(student));
        System.out.println(JSON.toJSONString(student));
        Thread.sleep(1000);
        list.add(student);
// 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (list.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            list.clear();
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        LOGGER.info("所有数据解析完成！");
        System.out.println("所有数据解析完成！");
    }

    private DemoDAO demoDAO = new DemoDAO();

    /**
     * 加上存储数据库
     */
    private void saveData() {
        LOGGER.info("{}条数据，开始存储数据库！", list.size());

        demoDAO.save(list);
        LOGGER.info("存储数据库成功！");
    }

}
