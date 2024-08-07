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

public class PAwaitHelloWorld3 {

   static String pnstr = "tcp://localhost/hello";
   //static int betweenPollSleepDurationMs = 20;
   static int betweenPollSleepDurationMs = 500; // slow for debugging

   public PAwaitHelloWorld3() {System.out.println("pahw2 constructor");}

   public static void main(String[] args) {
     System.out.println("p-await hello, world 2");
     Hose h = null;

     try {
       h = Pool.participate(pnstr);
       while (true) {
         Protein p=null; 
	 boolean awaitingProtein = true;

	 while (awaitingProtein) {
	   try { p = h.next(); awaitingProtein = false;
	   } catch (PoolException pe) {

  	     System.err.print('.');  System.err.flush();
	     try {Thread.sleep(betweenPollSleepDurationMs); // probably should confirm exception type
	     } catch (Exception e) {}                       // ditto
	   }
	 }

	 Slaw d=null, i=null;

	 if (p != null) {
	   d = p.descrips();
           i = p.ingests();
	 } else { System.err.println("Null protein noted; no idea why"); continue; }

	 if (d == null || i == null) {
           System.err.println("protein descrips or ingests is null; ignoring"); continue;
	 } 

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
