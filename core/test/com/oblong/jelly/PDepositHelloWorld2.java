// Brygg Ullmer: reduction to approximation of JShrake helloWorld example from Animist discord #plasma 
/// Deposit a protein into a pool.
// Begun August 7, 2024

import com.oblong.jelly.Slaw;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Hose;
import com.oblong.jelly.Pool;
import com.oblong.jelly.PoolException;

//#include "pool_cmd.h"
//#include "libLoam/c/ob-sys.h"
//#include "libLoam/c/ob-vers.h"
//#include "libPlasma/c/protein.h"
//#include "libPlasma/c/slaw.h"

public class PDepositHelloWorld2 {

  static String d1str = "name", d2str = "hello";
  static String istr  = "world";
  static String pnstr = "tcp://localhost/hello";

  public PDepositHelloWorld2() {System.out.println("pdhw2 constructor");}

  ///////////////// extract slaw ///////////////// 

  private Slaw extract_slaw (String arg) {
    //char *colon = strchr (arg, ':');
    int    colIdx   = arg.indexOf(':');
    if (colIdx == -1) { 
        System.err.println("error: ingest needs a colon to separate key and value:" + arg);
        System.exit(-1);
    }

    String valStr = arg.substring(colIdx);
    String keyStr = arg.substring(0, colIdx-1);

    Slaw key, value, pair;

    key   = Slaw.string(keyStr);
    value = Slaw.string(valStr); //C-based p-deposit can also handle int64 and float64; later

    //pair = slaw_cons_ff (key, value);
    pair = Slaw.cons(key, value); // Map<Slaw,Slaw> m = c.emitMap();
    return pair;
  }

  ///////////////// main ///////////////// 

   public static void main(String[] args) {
     System.out.println("p-deposit hello, world 2 begins");

     Hose h = null;

     try {
       h = Pool.participate(pnstr);
       Slaw descrips = Slaw.map(Slaw.string(d1str), Slaw.string(d2str));
       Slaw ingests  = Slaw.string(istr);
       Protein p = Slaw.protein(descrips, ingests);
       h.deposit(p);
     } catch (PoolException e) {
       System.out.println("Pool exception!");
       System.exit(-1);
     } finally {
       if (h != null) h.withdraw();
    }
    System.out.println("p-deposit hello, world 2 ends");
  } 
}
/// end ///
