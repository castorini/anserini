#!/usr/bin/perl -w

# Graded relevance assessment script for the TREC 2010 Web track
# Evalution measures are written to standard output in CSV format.
# 
# Currently reports only NDCG and ERR
# (see http://learningtorankchallenge.yahoo.com/instructions.php)

use constant LOGBASEDIV => log(2.0);

# gloals
my $QRELS;
my $VERSION = "version 1.3 (Mon Apr 29 20:50:24 EDT 2013)";
my $MAX_JUDGMENT = 4; # Maximum gain value allowed in qrels file.
my $K = 20;           # Reporting depth for results.
my $USAGE = "usage: $0 [options] qrels run\n
  options:\n
    -c
        Average over the complete set of topics in the relevance judgments
        instead of the topics in the intersection of relevance judgments 
        and results.\n
    -k value
        Non-negative integer depth of ranking to evaluate in range [1,inf].
        Default value is k=@{[($K)]}.\n
    -baseline BASELINE_RUN_FILE
        Baseline run to use for risk-sensitive evaluation\n
    -riskAlpha value
        Non-negative Risk sensitivity value to use when doing risk-sensitive
        evaluation.  A baseline must still be specified.  By default 0.
        The final weight to downside changes in performance is (1+value).\n";

  

use strict 'vars';

{ # main block to scope variables
  if ($#ARGV >= 0 && ($ARGV[0] eq "-v" || $ARGV[0] eq "-version")) {
    print "$0: $VERSION\n";
    exit 0;
  }

  my $baselineRun = undef;
  my $riskAlpha = 0;
  my $cflag = 0;
  while ($#ARGV != 1) # should probably replace this with perl's argument parsing
  {
    if ($#ARGV >= 0 && $ARGV[0] eq "-help") {
      print "$USAGE\n";
      exit 0;
    }
    elsif ($#ARGV >= 2 and ("-c" eq $ARGV[0]))
    {
      $cflag = 1;
      shift @ARGV; 
    }
    elsif ($#ARGV >= 3 and ("-k" eq $ARGV[0]))
    {
      $K = int($ARGV[1]);
      die $USAGE if ($K < 1);
#      print STDERR "k=$K\n";
      shift @ARGV; shift @ARGV;
    }
    elsif ($#ARGV >= 3 and ("-baseline" eq $ARGV[0]))
    {
      $baselineRun = $ARGV[1];
      shift @ARGV; shift @ARGV;
    }
    elsif ($#ARGV >= 3 and ("-riskAlpha" eq $ARGV[0]))
    {
      $riskAlpha = $ARGV[1];
      die $USAGE if ($riskAlpha < 0.0);
      shift @ARGV; shift @ARGV;
    }
    else
    {
      die $USAGE;
    }
  }

  die $USAGE unless $#ARGV == 1;
  $QRELS = $ARGV[0];
  my $run = $ARGV[1];

  # Read qrels file, check format, and sort
  my @qrels = ();
  my %seen = ();
  open (QRELS,"<$QRELS") || die "$0: cannot open \"$QRELS\": $!\n";
  while (<QRELS>) {
    s/[\r\n]//g;
    my ($topic, $zero, $docno, $judgment) = split (' ');
    $topic =~ s/^.*\-//;
    die "$0: format error on line $. of \"$QRELS\"\n"
	unless
	$topic =~ /^[0-9]+$/ && $zero == 0
	&& $judgment =~ /^-?[0-9]+$/ && $judgment <= $MAX_JUDGMENT;
    if ($judgment > 0) {
      $qrels[$#qrels + 1]= "$topic $docno $judgment";
      $seen{$topic} = 1;
    }
  }
  close (QRELS);
  @qrels = sort qrelsOrder (@qrels);

  # Process qrels: store judgments and compute ideal gains
  my $topicCurrent = -1;
  my %ideal = ();
  my @gain = ();
  my %judgment = ();
  for (my $i = 0; $i <= $#qrels; $i++) {
    my ($topic, $docno, $judgment) = split (' ', $qrels[$i]);
    if ($topic != $topicCurrent) {
      if ($topicCurrent >= 0) {
	$ideal{$topicCurrent} = &dcg($K, @gain);
	$#gain = -1;
      }
      $topicCurrent = $topic;
    }
    next if $judgment < 0;
    $judgment{"$topic:$docno"} = $gain[$#gain + 1] = $judgment;
  }
  if ($topicCurrent >= 0) {
    $ideal{$topicCurrent} = &dcg($K, @gain);
    $#gain = -1;
  }

  # process baseline if doing risk sensitive
  my ($baseNDCGByTopic,$baseERRByTopic,$baserunname);
  if (defined $baselineRun)
  {
    ($baseNDCGByTopic,$baseERRByTopic,$baserunname) = processRun($baselineRun,0,\%seen,\%ideal,\%judgment,$cflag,0);
  }

  # process main run
  processRun($run,1,\%seen,\%ideal,\%judgment,$cflag,defined($baselineRun),$riskAlpha,$baserunname,$baseNDCGByTopic,$baseERRByTopic);

  exit 0;

} # end main block

# comparison function for qrels: by topic then judgment
sub qrelsOrder {
  my ($topicA, $docnoA, $judgmentA) = split (' ', $a);
  my ($topicB, $docnoB, $judgmentB) = split (' ', $b);

  if ($topicA < $topicB) {
    return -1;
  } elsif ($topicA > $topicB) {
    return 1;
  } else {
    return $judgmentB <=> $judgmentA;
  }
}

# comparison function for runs: by topic then score then docno
sub runOrder {
  my ($topicA, $docnoA, $scoreA) = split (' ', $a);
  my ($topicB, $docnoB, $scoreB) = split (' ', $b);

  if ($topicA < $topicB) {
    return -1;
  } elsif ($topicA > $topicB) {
    return 1;
  } elsif ($scoreA < $scoreB) {
    return 1;
  } elsif ($scoreA > $scoreB) {
    return -1;
  } elsif ($docnoA lt $docnoB) {
    return 1;
  } elsif ($docnoA gt $docnoB) {
    return -1;
  } else {
    return 0;
  }
}

# compute DCG over a sorted array of gain values, reporting at depth $k
sub dcg {
 my ($k, @gain) = @_;
 my ($i, $score) = (0, 0);

 for ($i = 0; $i <= ($k <= $#gain ? $k - 1 : $#gain); $i++) {
   $score += (2**$gain[$i] - 1)/(log ($i + 2)/ +LOGBASEDIV);
 }
 return $score;
}

# compute ERR over a sorted array of gain values, reporting at depth $k
sub err {
  my ($k, @gain) = @_;
  my ($i, $score, $decay, $r);

 $score = 0.0;
 $decay = 1.0;
 for ($i = 0; $i <= ($k <= $#gain ? $k - 1 : $#gain); $i++) {
   $r = (2**$gain[$i] - 1)/(2**$MAX_JUDGMENT);
   $score += $r*$decay/($i + 1);
   $decay *= (1 - $r);
 }
 return $score;
}

sub riskWeighted
{
  my ($run,$base,$alpha) = @_;
  if ($run < $base)
  {
    $run = (1+$alpha) * ($run - $base);
  }
  else
  {
    $run = $run - $base;
  }
  return $run;
}
# compute and report information for current topic
sub topicDone {
  my ($printTopic, $runid, $topic, $pndcgTotal, $perrTotal, $ptopics, $pseen, $pideal, 
      $isRiskSensitive, $riskAlpha, $baseNDCG, $baseERR, @gain) = @_;
  my($ndcg, $err) = (0, 0);
  if (exists($$pseen{$topic}) and defined($$pseen{$topic}) and $$pseen{$topic}) {
    $ndcg = &dcg($K, @gain)/$$pideal{$topic};
    $err = &err ($K, @gain);
    $ndcg = riskWeighted($ndcg,$baseNDCG,$riskAlpha) if ($isRiskSensitive);
    $err = riskWeighted($err,$baseERR,$riskAlpha) if ($isRiskSensitive);
    $$pndcgTotal += $ndcg;
    $$perrTotal += $err;
    $$ptopics++;
    printf("$runid,$topic,%.5f,%.5f\n",$ndcg,$err) if ($printTopic);
    return ($ndcg,$err);
  }
}

sub processRun
{
  my ($run,$printTopics,$pseen,$pideal,$pjudgment,$avgOverAllTopics,$isRiskSensitive,$riskAlpha,$baserunname,$baseNDCGByTopic,$baseERRByTopic) = @_;
  my $ndcgByTopic = {()};
  my $errByTopic = {()};
  my $runid = "?????";
  my @run = ();
  # Read run rile, check format, and sort
  open (RUN,"<$run") || die "$0: cannot open \"$run\": $!\n";
  while (<RUN>) {
    s/[\r\n]//g;
    my ($topic, $q0, $docno, $rank, $score);
    ($topic, $q0, $docno, $rank, $score, $runid) = split (' ');
    $topic =~ s/^.*\-//;
    die "$0: format error on line $. of \"$run\"\n"
	unless
	$topic =~ /^[0-9]+$/ && $q0 eq "Q0" && $rank =~ /^[0-9]+$/ && $runid;
    $run[$#run + 1] = "$topic $docno $score";
  }

  @run = sort runOrder (@run);

  my %processed = ();
  foreach my $topic (%$pseen)
  {
    $processed{$topic} = 0;
  }

  if ($isRiskSensitive)
  {
    $runid = sprintf("%s (rel to. %s, rs=1+a, a=%s)",$runid,$baserunname,$riskAlpha);
  }

  # Process runs: compute measures for each topic and average
  my $ndcgTotal = 0;
  my $errTotal = 0;
  my $topics = 0;
  print "runid,topic,ndcg\@$K,err\@$K\n" if ($printTopics);
  my $topicCurrent = -1;
  my @gain = ();
  for (my $i = 0; $i <= $#run; $i++) {
    my ($topic, $docno, $score) = split (' ', $run[$i]);
    if ($topic != $topicCurrent) {
      if ($topicCurrent >= 0) {
	my ($baseNDCG,$baseERR) = 0;
	if ($isRiskSensitive)
	{
	  $baseNDCG = $$baseNDCGByTopic{$topicCurrent} if (exists($$baseNDCGByTopic{$topicCurrent}) and defined($$baseNDCGByTopic{$topicCurrent}));
	  $baseERR = $$baseERRByTopic{$topicCurrent} if (exists($$baseERRByTopic{$topicCurrent}) and defined($$baseERRByTopic{$topicCurrent}));
	}
	my ($ndcg,$err) = &topicDone ($printTopics, $runid, $topicCurrent, \$ndcgTotal, \$errTotal, \$topics, 
				      $pseen, $pideal, $isRiskSensitive, $riskAlpha, $baseNDCG, $baseERR, @gain);
	$$ndcgByTopic{$topicCurrent} = $ndcg;
	$$errByTopic{$topicCurrent} = $err;
	$processed{$topicCurrent} = 1;
	$#gain = -1;
      }
      $topicCurrent = $topic;
    }
    my $j  = $$pjudgment{"$topic:$docno"};
    $j = 0 unless $j;
    $gain[$#gain + 1] = $j;
  }
  if ($topicCurrent >= 0) {
    my ($baseNDCG,$baseERR) = 0;
    if ($isRiskSensitive)
    {
      $baseNDCG = $$baseNDCGByTopic{$topicCurrent} if (exists($$baseNDCGByTopic{$topicCurrent}) and defined($$baseNDCGByTopic{$topicCurrent}));
      $baseERR = $$baseERRByTopic{$topicCurrent} if (exists($$baseERRByTopic{$topicCurrent}) and defined($$baseERRByTopic{$topicCurrent}));
    }
    my ($ndcg,$err) = &topicDone ($printTopics, $runid, $topicCurrent, \$ndcgTotal, \$errTotal, \$topics, 
				  $pseen, $pideal, $isRiskSensitive, $riskAlpha, $baseNDCG, $baseERR, @gain);
    $$ndcgByTopic{$topicCurrent} = $ndcg;
    $$errByTopic{$topicCurrent} = $err;
    $processed{$topicCurrent} = 1;
    $#gain = -1;
  }
  my $numTopics = $topics;  # $topics has the number in the run (at this point)
  if ($avgOverAllTopics)
  {
    $numTopics = scalar(keys %$pseen); # we want denominator to change whenever flag is on but only need to compute differences for risk
    if ($isRiskSensitive)
    { # need to process any topics that were missing from run
	my ($baseNDCG,$baseERR) = 0;
	my @gain = ();
	foreach my $topicCurrent (sort {$a <=> $b} keys %processed)
	{
	  next if ($processed{$topicCurrent});
	  $baseNDCG = $$baseNDCGByTopic{$topicCurrent} if (exists($$baseNDCGByTopic{$topicCurrent}) and defined($$baseNDCGByTopic{$topicCurrent}));
	  $baseERR = $$baseERRByTopic{$topicCurrent} if (exists($$baseERRByTopic{$topicCurrent}) and defined($$baseERRByTopic{$topicCurrent}));
	  my ($ndcg,$err) = &topicDone ($printTopics, $runid, $topicCurrent, \$ndcgTotal, \$errTotal, \$topics, 
					$pseen, $pideal, $isRiskSensitive, $riskAlpha, $baseNDCG, $baseERR, @gain);
	}
      }
  }

  my $ndcgAvg = $ndcgTotal;
  my $errAvg = $errTotal;
  if ($numTopics > 0)
  {
    $ndcgAvg  /= $numTopics;
    $errAvg /= $numTopics;
  }
  printf "$runid,amean,%.5f,%.5f\n",$ndcgAvg,$errAvg if ($printTopics);
  
  return ($ndcgByTopic,$errByTopic,$runid);
  close(RUN);
}
