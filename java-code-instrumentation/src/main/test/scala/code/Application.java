package code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);


    public static void main(String[] args) {

        int a = 1;
        logger.info("Logging a variable declaration and its value a : {}", a);
        int b = 1;
        logger.info("Logging a variable declaration and its value a : {}", a);

    }

}