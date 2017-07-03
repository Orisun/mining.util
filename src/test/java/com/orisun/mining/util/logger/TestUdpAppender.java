package com.orisun.mining.util.logger;

import com.orisun.mining.util.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestUdpAppender {

    @BeforeClass
    public static void setup() {
        String logFile = Path.getCurrentPath() + "config/log4j.properties";
        PropertyConfigurator.configure(logFile);
    }

    @Test
    public void test() throws Exception {
        Log logger = LogFactory.getLog("udp");

        for (int i = 0; i < 200; ) {
            int pageNo = i/10 + 1 ;
            String content = "\tAPP_HOME\t6087505\t"+String.valueOf(pageNo);
            for(int j=0; j< 10; j++, i++){
                content+="\t"+String.valueOf(i)+",dm-rec-interest_cf-1";  
            }
            logger.info(content);
            Thread.sleep(20);
        }
    }
}
