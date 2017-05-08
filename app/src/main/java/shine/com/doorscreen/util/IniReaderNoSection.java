package shine.com.doorscreen.util;


import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
/*
* 无Section的配置文件读取*/
public class IniReaderNoSection {
    public Properties properties = null;

    public IniReaderNoSection(String filename) {
        File file = new File(filename);
        try {
            properties = new Properties();
            properties.load(new FileInputStream(file));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getValue(String key) {
        return properties.getProperty(key, "");

    }
}

