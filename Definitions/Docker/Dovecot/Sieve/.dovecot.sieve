require "spamtestplus";
require "virustest";
require "fileinto";
require "relational";
require "comparator-i;ascii-numeric";

/* If the spamtest fails for some reason, e.g. spam header is missing, file
 * file it in a special folder.
 */
if spamtest :value "eq" :comparator "i;ascii-numeric" "0" {
  fileinto "INBOX/Unclassified";

/* If the spamtest score (in the range 1-10) is larger than or equal to 3,
 * file it into the spam folder:
 */
} elsif spamtest :value "ge" :comparator "i;ascii-numeric" "3" {
  fileinto "INBOX/Spam";

/* For more fine-grained score evaluation, the :percent tag can be used. The
 * following rule discards all messages with a percent score
 * (relative to maximum) of more than 85 %:
 */
} elsif spamtest :value "gt" :comparator "i;ascii-numeric" :percent "85" {
  discard;
}

/* Not scanned ? */
if virustest :value "eq" :comparator "i;ascii-numeric" "0" {
  fileinto "INBOX/Unscanned";

/* Infected with high probability (value range in 1-5) */
} if virustest :value "eq" :comparator "i;ascii-numeric" "4" {
  /* Quarantine it in special folder (still somewhat dangerous) */
  fileinto "INBOX/Quarantine";

/* Definitely infected */
} elsif virustest :value "eq" :comparator "i;ascii-numeric" "5" {
  /* Just get rid of it */
  discard;
}