
/* (c)  oblong industries */

import static com.oblong.jelly.Slaw.*;
import static com.oblong.jelly.Protein.*;

public class PAwaitHelloWorld2 {

  public PAwaitHelloWorld2() {System.out.println("pahw2 constructor");}

  public static void main(String[] args) {
    System.out.println("p-await hello, world 2");
  }
}

///
/// Loop forever reading proteins from a pool with pool_await_next().

/*///
#include "pool_cmd.h"
#include "libLoam/c/ob-sys.h"
#include "libLoam/c/ob-vers.h"
#include "libLoam/c/ob-log.h"
#include "libPlasma/c/slaw.h"
#include "libPlasma/c/slaw-io.h"
#include "libPlasma/c/protein.h"

static void usage (void)
{
  ob_banner (stderr);
  fprintf (stderr, "Usage: p-await <pool name>\n");
  exit (EXIT_FAILURE);
}

int main (int argc, char **argv)
{
  OB_CHECK_ABI ();

  ob_retort pret;
  protein p;
  pool_timestamp ts;
  pool_cmd_info cmd;

  memset(&cmd, 0, sizeof(cmd));

  const char *pnstr = "tcp://localhost/hello";
  cmd.pool_name     = pnstr;

  //if (pool_cmd_get_poolname (&cmd, argc, argv, optind))
  //  usage ();

  pool_cmd_open_pool (&cmd);

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
}*?*?*?*?
