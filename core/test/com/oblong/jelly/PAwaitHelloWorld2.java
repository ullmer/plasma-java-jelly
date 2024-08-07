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
       Slaw descrips = Slaw.list(Slaw.string("hello"));
       Slaw ingests  = Slaw.map(Slaw.string("name"), Slaw.string("world"));
       Protein p = Slaw.protein(descrips, ingests);
       h.deposit(p);
     //} catch (Exception e) { 
     } catch (PoolException e) {
       System.out.println("Pool exception:" + e);
       System.exit(-1);
     } finally {
       if (h != null) h.withdraw();
    }
    System.out.println("p-deposit hello, world 3 ends");
  } 
}


  while (1)
    {
      pret = pool_await_next (cmd.ph, POOL_WAIT_FOREVER, &p, &ts, NULL);
      if (OB_OK != pret)
        {
          pool_withdraw (cmd.ph);
          fprintf (stderr, "problem with pool_await_next(): %s\n",
                   ob_error_string (pret));
          return pool_cmd_retort_to_exit_code (pret);
        }
      bslaw d = protein_descrips(p);
      bslaw i = protein_ingests(p);

      //slaw *sl = malloc(1000);
      //slaw_to_string(d, sl);
      //char *st = slaw_string_emit(d);

      char *str1 = slaw_list_emit_first(d);
      printf("STR1: %s\n", str1); 

      void *map = slaw_list_emit_first(i);


      //char *str2 = slaw_cons_emit_cdr(i);
      //char *str3 = slaw_cons_emit_car(i);

      char *str2 = slaw_cons_emit_car(map);
      char *str3 = slaw_cons_emit_cdr(map);

      printf("STR2: %s\n", str2); 
      printf("STR3: %s\n", str3); 
      
      fflush(stdout);
      //slaw_spew_overview (p, stdout, NULL);
      printf("I3\n");
      slaw_spew_overview (i, stdout, NULL);
      fputc ('\n', stdout);
      protein_free (p);
    }

  // Not reached at present.
  OB_DIE_ON_ERROR (pool_withdraw (cmd.ph));
  pool_cmd_free_options (&cmd);

  return EXIT_SUCCESS;
}*/


/// end ///
