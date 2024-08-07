// Brygg Ullmer: reduction to approximation of JShrake helloWorld example from Animist discord #plasma 
/// Deposit a protein into a pool.

import static com.oblong.jelly.Slaw.*;
import static com.oblong.jelly.Protein.*;

#include "pool_cmd.h"
#include "libLoam/c/ob-sys.h"
#include "libLoam/c/ob-vers.h"
#include "libPlasma/c/protein.h"
#include "libPlasma/c/slaw.h"

static slaw extract_slaw (char *arg)
{
  char *colon = strchr (arg, ':');
  slaw key, value, pair;

  if (colon == NULL)
    { fprintf (stderr, "error: ingest '%s' needs a colon to separate key and value\n", arg);
      exit (EXIT_FAILURE);
    }

  char *keystr = (char *) malloc (colon - arg + 1);
  strncpy (keystr, arg, colon - arg);
  keystr[colon - arg] = '\0';
  key = slaw_string (keystr);
  free (keystr);

  do
    { char *endptr;
      int64 int_val = strtol (colon + 1, &endptr, 10);
      if (*endptr == '\0')
        { value = slaw_int64 (int_val);
          break;
        }
      float64 float_val = strtod (colon + 1, &endptr);
      if (*endptr == '\0')
        { value = slaw_float64 (float_val);
          break;
        }
      value = slaw_string (colon + 1);
    }
  while (0);

  pair = slaw_cons_ff (key, value);
  return pair;
}

int main (int argc, char **argv)
{ OB_CHECK_ABI ();

  ob_retort     pret;
  pool_cmd_info cmd;
  protein       prot;
  slaw          ingest;
  int           c;

  //jshrake hello-world example
  //https://media.discordapp.net/attachments/1235355937586413578/1252001896156102736/image.png?ex=669196a1&is=66904521&hm=fb7b16cd19a85a6723e48956e91f3a262007e2b5db71b99be5a96a191585787c

  const char *dstr  = "hello";
  const char *istr  = "name:world";
  const char *pnstr = "tcp://localhost/hello";

  memset(&cmd, 0, sizeof(cmd));

  slabu *descrips = slabu_new ();
  slabu *ingests  = slabu_new ();

  ingest = extract_slaw (istr);
  OB_DIE_ON_ERROR (slabu_list_add_c (descrips, dstr));
  OB_DIE_ON_ERROR (slabu_list_add_x (ingests, ingest));

  cmd.verbose   = 1;
  cmd.pool_name = pnstr;

  pool_cmd_open_pool (&cmd);

  prot = protein_from_ff (slaw_list_f (descrips), slaw_map_f (ingests));
  if (cmd.verbose)
    { fprintf (stderr, "depositing in %s\n", cmd.pool_name);
      slaw_spew_overview (prot, stderr, NULL);
    }

  pret = pool_deposit (cmd.ph, prot, NULL);
  protein_free (prot);

  if (OB_OK != pret)
    { fprintf (stderr, "no luck on the deposit: %s\n", ob_error_string (pret));
      exit (pool_cmd_retort_to_exit_code (pret));
    }

  OB_DIE_ON_ERROR (pool_withdraw (cmd.ph));

  return EXIT_SUCCESS;
}

/// end ///
