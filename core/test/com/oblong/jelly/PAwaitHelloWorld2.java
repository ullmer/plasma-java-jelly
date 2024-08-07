// Brygg Ullmer: reduction to approximation of JShrake helloWorld example from Animist discord #plasma 
/// Await a protein from a pool.
// Begun August 7, 2024
//
import com.oblong.jelly.Slaw;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Hose;
import com.oblong.jelly.Pool;
import com.oblong.jelly.PoolException;

import static com.oblong.jelly.Slaw.*;
import static com.oblong.jelly.Protein.*;

public class PAwaitHelloWorld2 {

   static String pnstr = "tcp://localhost/hello";

   public PAwaitHelloWorld2() {System.out.println("pahw2 constructor");}

   public static void main(String[] args) {
     System.out.println("p-await hello, world 2");
     Hose h = null;

     try {
       h = Pool.participate(pnstr);
       while (true) {
         Protein p = h.next();

	 Slaw d = p.descrips();
         Slaw i = p.ingests();

	 if (d.isList()) {
           //String str1 = d.nth(0).emitString();
           String str1 = d.car().emitString();
	   System.out.println("STR1:" + str1);
	 } else {
           System.err.println("Message received; descrips is not a list");
	 }

	 if (i.isMap()) {
           String str2 = i.car().emitString();
	   String str3 = i.cdr().emitString();

	   System.out.println("STR2:" + str2);
	   System.out.println("STR2:" + str3);
	 } else {
           System.err.println("Message received; ingests is not a map");
	 }

	 p.free()
     } catch (PoolException e) {
       System.out.println("Pool exception:" + e);
       System.exit(-1);
     } finally {
       if (h != null) h.withdraw();
    }
    System.out.println("p-await hello, world 3 ends");
  } 
}

/// end ///
