package common;

import java.io.FileReader;
import java.io.IOException;

public class DataProperties {

    public static java.util.Properties getProperties() {
        String propPath = "src/main/resources/props.properties";
        java.util.Properties props = new java.util.Properties();
        try {
            props = getProperties(propPath);
        }
        catch(Exception e){
            propPath = "./properties.properties";
            try {
                props = getProperties(propPath);
            } catch (Exception e1) {
                e1.printStackTrace();
                e.printStackTrace();
            }
        }
        return props;
    }

    public static java.util.Properties getProperties(String propPath) throws IOException {
        java.util.Properties prop = new java.util.Properties();
        prop.load(new FileReader(propPath));
        return prop;
    }
}
