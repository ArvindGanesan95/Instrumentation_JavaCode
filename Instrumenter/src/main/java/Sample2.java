
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Sample2{  
private static Logger logger = LoggerFactory.getLogger(Sample2.class);

public static void main(String args[])  
{    
 logger.info("Logging a method invocation with name : print()");
	logger.info("Logging a method declaration with name : main()");
int n1=0,n2=1,n3,i,count=10;
logger.info("Logging a variable declaration and its value n1 : {}", n1);    
 System.out.print(n1+" "+n2);//printing 0 and 1    
    
 logger.info("Logging a for statement");
for(i=2;i<count;++i)//loop starts from 2 because 0 and 1 are already printed    
 {    
  logger.info("Logging a method invocation with name : print()");
n3=n1+n2;    
  System.out.print(" "+n3);    
  n1=n2;    
  n2=n3;    
 }    
  
}} 