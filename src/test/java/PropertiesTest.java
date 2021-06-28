import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

public class PropertiesTest {
    @Test
    public void test(){
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
        while (iterator.hasNext()){
            String next = iterator.next();
            String property = prop.getProperty(next);
            String[] split = property.split(",");
          //  System.out.println(split[0]);
           // System.out.println(split[1]);
        }
        //D:\hadoopproject\excel\excelDemo01\src\main\java\com\xuelove\excel\pojo\
        String packageOutPath = "com.xuelove.excel.pojo";
        // 创建文件对象
        File directory = new File("");
        String outputPath = directory.getAbsolutePath() + "\\src\\" + packageOutPath.replace(".", "\\") + "\\";
        System.out.println(outputPath);
    }
}
