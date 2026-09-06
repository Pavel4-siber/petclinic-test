package loadProperties;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


public interface ConfigProvider {
    Config config = loadConfig();

    static Config loadConfig() {
        return ConfigFactory.systemProperties().hasPath("testProfile")
                ? ConfigFactory.load(ConfigFactory.systemProperties().getString("testProfile"))
                : ConfigFactory.load("application.conf");
    }

    String URL = loadConfig().getString("url");

}
