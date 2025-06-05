// Brygg Ullmer: reduction to approximation of JShrake helloWorld example from Animist discord #plasma 
/// Deposit a protein into a pool.
// Begun August 7, 2024

import com.oblong.jelly.Slaw;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Hose;
import com.oblong.jelly.Pool;
import com.oblong.jelly.PoolException;

public class PDepositHelloWorld3 {
  static String pnstr = "tcp://localhost/hello";

  public PDepositHelloWorld3() {System.out.println("pdhw2 constructor");}

  ///////////////// main ///////////////// 

   public static void main(String[] args) {
     System.out.println("p-deposit hello, world 3 begins");

     Hose h = null;

     try {
       h = Pool.participate(pnstr);
       Slaw descrips = Slaw.list(Slaw.string("hello"));
       Slaw ingests  = Slaw.map(Slaw.string("name"), Slaw.string("world"));
       Protein p = Slaw.protein(descrips, ingests);
       h.deposit(p);
     } catch (PoolException e) {
       System.out.println("Pool exception:" + e);
       System.exit(-1);
     } finally {
       if (h != null) h.withdraw();
    }
    System.out.println("p-deposit hello, world 3 ends");
  } 
}
/// end ///
