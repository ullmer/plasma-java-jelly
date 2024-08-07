// Brygg Ullmer: reduction to approximation of JShrake helloWorld example from Animist discord #plasma 
/// Synchronously await a protein from a pool (with blocking awaitNext call).  
///  Sister variant PAwaitHelloWorld3 will hopefully demonstrate async version.
// Begun August 7, 2024

import java.util.Map; 
import java.util.HashMap; 

import java.lang.Thread;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Hose;
import com.oblong.jelly.Pool;
import com.oblong.jelly.PoolException;

public class PAwaitHelloWorld2 {

   static String pnstr = "tcp://localhost/hello";
   //int betweenPollSleepDurationMs = 20;
   int betweenPollSleepDurationMs = 500;

   public PAwaitHelloWorld2() {System.out.println("pahw2 constructor");}

   public static void main(String[] args) {
     System.out.println("p-await hello, world 2");
     Hose h = null;

     try {
       h = Pool.participate(pnstr);
       while (true) {
         Protein p;
	 boolean awaitingProtein = true;

	 while (awaitingProtein) {
	   try { p = h.awaitNext();
		 awaitingProtein = false;
	   } catch (PoolException e) {
  	     System.err.print('.');  System.err.flush();
             Thread.sleep(betweenPollSleepDurationMs); // probably should confirm exception type
	   }
	 }

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
           Map<Slaw,Slaw> imap = i.emitMap();

	   for (Map.Entry<Slaw,Slaw> entry : imap.entrySet()) {
              Slaw key   = entry.getKey();
	      Slaw value = entry.getValue();

	      if (key.isString() && value.isString()) {
                String str2 = key.emitString();
                String str3 = value.emitString();
	
	        System.out.println("STR2:" + str2);
    	        System.out.println("STR2:" + str3);
	     } else {
               System.err.println("Message received; ingests is a map, but entry does not map strings to strings");
	     }
	   }
	 } else {
           System.err.println("Message received; ingests is not a map");
	 }
       }
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
