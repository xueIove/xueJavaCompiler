package com.xuelove.excel.Refactor.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.xuelove.excel.Refactor.ExcelDemo;


import javax.tools.JavaFileObject;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class WriteDemo {
    private String packageOutPath = "com.xuelove.excel.pojo";// 指定实体生成所在包的路径
    private static Map<String, JavaFileObject> fileObjects = new ConcurrentHashMap<>();
    private String tablename;// 表名
    private static Class<?> clazz ;//类型的class
    private List<String> colnames = new ArrayList(); // 列名集合
    private List<String> colTypes = new ArrayList(); // 列名类型集合
    private boolean f_util = false; // 是否需要导入包java.util.*
    private boolean f_sql = false; // 是否需要导入包java.sql.*

    public WriteDemo() {
        //使用properties读取配置文件
        Properties prop = new Properties();
        try {
            InputStream genentity = getClass().getResourceAsStream("/student.properties");
            prop.load(genentity);
            if (genentity != null) {
                genentity.close();
            }
        } catch
        (Exception e) {
            System.out.println("file " + "catalogPath.properties" +
                    " not found!\n" + e);
        }
        Iterator<String> iterator = prop.stringPropertyNames().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            String property = prop.getProperty(next);
            String[] split = property.split(",");
            //属性
            colnames.add(split[0]);
            //类型
            colTypes.add(split[1]);
        }
    }

    /**
     * 创建多个实体类
     *
     * @param tablenames 表名集合
     */
    public void genEntity(List<String> tablenames) {
        // 使用第归生成文件
        for (String tablename : tablenames) {
            this.genEntity(tablename);
        }
    }

    /**
     * 创建单个实体类
     *
     * @param tablename 表名
     * @param
     */
    public void genEntity(String tablename) {
        // 在内存中生成代码
        String code = parse(tablename);
        System.out.println(code);
        App app = new App();
        String cls = app.MyJavaCompiler(code);
        ClassLoader classloader = new App.MyClassLoader();
        try {
            clazz = classloader.loadClass(cls);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在内存中生成代码
     *
     * @param tablename 表名
     * @return 返回代码
     */
    public String parse(String tablename) {
        StringBuffer sb = new StringBuffer();
        // 判断是否导入工具包
        if (f_util) {
            sb.append("import java.util.Date;\r\n");
        }
        if (f_sql) {
            sb.append("import java.sql.*;\r\n");
        }
        // 导入对应的包（实体类所在的包 src后的包）
        // sb.append("package " + this.packageOutPath + ";\r\n");
        //  sb.append("\r\n");
        // 注释部分
        //sb.append(" /**\r\n");
        //sb.append(" * " + tablename + " 实体类\r\n");
        // 获取系统时间
        String da = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // 时间加作者名
        // sb.append(" * " + da + " " + this.authorName + "\r\n");
     //   sb.append(" */ \r\n");
        // 实体部分
        sb.append("\r\n\r\npublic class " + initcap(tablename) + "{\r\n");
        processAllAttrs(sb);// 属性
        processAllMethod(sb);// get set方法
        //  processGz(sb);// 有参构造函数 无参构造函数
        // processToString(sb);// toString方法
        sb.append("}\r\n");

        return sb.toString();
    }

    /**
     * 功能：生成所有属性
     *
     * @param sb
     */
    public void processAllAttrs(StringBuffer sb) {


        // 遍历字段集合
        for (int i = 0; i < colnames.size(); i++) {
            // 拼写属性 sqlType2JavaType把数据库字段类型转为java对应的数据类型
            sb.append("\tprivate " + sqlType2JavaType((String) colTypes.get(i)) + " " + colnames.get(i) + ";\r\n");
            System.out.println(sqlType2JavaType((String) colTypes.get(i)) + "返回的类型");
        }
    }

    /**
     * 功能：生成所有方法
     *
     * @param sb
     */
    public void processAllMethod(StringBuffer sb) {
        // 遍历字段集合
        for (int i = 0; i < colnames.size(); i++) {
            // 拼写 set方法
            sb.append("\tpublic void set" + initcap(colnames.get(i)) + "(" + sqlType2JavaType(colTypes.get(i)) + " "
                    + colnames.get(i) + "){\r\n");
            sb.append("\t\tthis." + colnames.get(i) + "=" + colnames.get(i) + ";\r\n");
            sb.append("\t}\r\n");

            // 拼写get方法
            sb.append("\tpublic " + sqlType2JavaType(colTypes.get(i)) + " get" + initcap(colnames.get(i)) + "(){\r\n");
            sb.append("\t\treturn " + colnames.get(i) + ";\r\n");
            sb.append("\t}\r\n");
        }
    }

    /**
     * 生成有参构造函数 无参构造函数
     *
     * @param sb
     */
   /* public void processGz(StringBuffer sb) {
        // 有参构造函数
        sb.append("\tpublic " + initcap(tablename) + "(");
        // 遍历字段集合
        for (int i = 0; i < colnames.size() - 1; i++) {
            sb.append(sqlType2JavaType("varchar") + " " + colnames.get(i) + ",");
        }
        sb.append(sqlType2JavaType("varcher") + " " + colnames.get(colnames.size() - 1)
                + "){\r\n");
        // 遍历字段集合
        for (int i = 0; i < colnames.size(); i++) {
            sb.append("\t\tthis." + colnames.get(i) + "=" + colnames.get(i) + ";\r\n");
        }
        sb.append("\t}\r\n");

        // 无参构造函数
        sb.append("\tpublic " + initcap(tablename) + "(){\r\n");
        sb.append("\t}\r\n");
    }*/

    /**
     * toString方法
     *
     * @param sb
     */
   /* public void processToString(StringBuffer sb) {
        // toString
        sb.append("\tpublic String toString() {\r\n");
        sb.append("\t\treturn \"Student [");
        // 遍历字段集合
        for (int i = 0; i < colnames.size() - 1; i++) {
            sb.append(colnames.get(i) + "=\"+" + colnames.get(i) + "+\",");
        }
        sb.append(colnames.get(colnames.size() - 1) + "=\"+" + colnames.get(colnames.size() - 1) + "+\"]\";\r\n");
        sb.append("\t}\r\n");
    }*/

    /**
     * 功能：将输入字符串的首字母改成大写
     *
     * @param str
     * @return
     */
    public String initcap(String str) {
        // 转换为char数组
        char[] ch = str.toCharArray();
        // 如果是小字母
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            // 转换为大写
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 功能：获得列的数据类型
     *
     * @return 返回java对应的数据类型
     */
    public String sqlType2JavaType(String value) {
        String type = "Object";
        if (value.equalsIgnoreCase("String")) {
            /**
             * 设置字段类型
             */
            type = "String";
        } else if (value.equalsIgnoreCase("Integer")) {
            type = "Integer";
        } else if (value.equalsIgnoreCase("short")) {
            type = "Short";
        } else if (value.equalsIgnoreCase("byte")) {
            type = "Byte";
        } else if (value.equalsIgnoreCase("long")) {
            type = "Long";
        } else if (value.equalsIgnoreCase("boolean")) {
            type = "Boolean";
        } else if (value.equalsIgnoreCase("date")) {
            type = "java.util.Date";
        } else if (value.equalsIgnoreCase("double")) {
            type = "double";
        }
        return type;
    }


    /**
     * 出口 TODO
     *
     * @param args
     */
    public static void main(String[] args) {
        // 包名
        String packageOutPath = "com.xuelove.excel.pojo";
        // 作者名
        String authorName = "铁锤";
        // 表名
        String tablename = "ChangLong";
        // 数据库名
        String databasename = "t222";
        //  WriteDemo writeDemo = new WriteDemo(packageOutPath, authorName, tablename, databasename);
        WriteDemo writeDemo = new WriteDemo();
        writeDemo.genEntity(tablename);
        // 为每个实体表生成实体类
        //  genEntity(tablenames);
        String fileName = WriteDemo.class.getResource("/demo.xlsx").getPath();
        System.out.println(fileName);
        ExcelReader excelReader = null;

        try {
            excelReader = EasyExcel.read(fileName, clazz, new ExcelDemo()).build();
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            ExcelReader read = excelReader.read(readSheet);
            System.out.println(read);
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }

    }


}
