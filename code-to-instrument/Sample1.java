
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Sample1{  
private static Logger logger = LoggerFactory.getLogger(Sample2.class);

public static void main(String args[])  
{    
 int n5=0,n6=1,n4,i,count=10,k=8;    
 System.out.print(n5+" "+n6);//printing 0 and 1    
    
 for(i=2;i<count;++i)//loop starts from 2 because 0 and 1 are already printed    
 {    
  n4=n5+n6;    
  System.out.print(" "+n5);    
  n5=n6;       
 }    
  
}} 