
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Sample1{  
private static Logger logger = LoggerFactory.getLogger(Sample2.class);

public static void main(String args[])  
{    
 logger.info("Logging a method invocation with name : print()");
	logger.info("Logging a method declaration with name : main()");
int n5=0,n6=1,n4,i,count=10,k=8;
logger.info("Logging a variable declaration and its value n5 : {}", n5);    
 System.out.print(n5+" "+n6);//printing 0 and 1    
    
 logger.info("Logging a for statement");
for(i=2;i<count;++i)//loop starts from 2 because 0 and 1 are already printed    
 {    
  logger.info("Logging a method invocation with name : print()");
n4=n5+n6;    
  System.out.print(" "+n5);    
  n5=n6;       
 }    
  
}} 