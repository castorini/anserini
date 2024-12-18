BIN = /usr/local/bin
#   Copyright (c) 2008 - Chris Buckley. 
#
#   Permission is granted for use and modification of this file for
#   research, non-commercial purposes. 

H   = .

VERSIONID = 9.0.7

# gcc
CC       = gcc
#CFLAGS   = -g -I$H  -Wall -DVERSIONID=\"$(VERSIONID)\"
#CFLAGS   = -g -I$H  -Wall -DMDEBUG -DVERSIONID=\"$(VERSIONID)\"
#CFLAGS   = -pg -I$H -O3 -Wall -DVERSIONID=\"$(VERSIONID)\"
#CFLAGS   = -g -I$H -O3 -Wall -DVERSIONID=\"$(VERSIONID)\"
CFLAGS   = -g -I$H  -Wall -DVERSIONID=\"$(VERSIONID)\"

# Other macros used in some or all makefiles
INSTALL = /bin/mv

TOP_SRCS = trec_eval.c formats.c meas_init.c meas_acc.c meas_avg.c \
	meas_print_single.c meas_print_final.c

FORMAT_SRCS = get_qrels.c get_trec_results.c get_prefs.c get_qrels_prefs.c \
	get_qrels_jg.c form_res_rels.c form_res_rels_jg.c \
        form_prefs_counts.c \
        utility_pool.c get_zscores.c convert_zscores.c

MEAS_SRCS =  measures.c  m_map.c m_P.c m_num_q.c m_num_ret.c m_num_rel.c \
        m_num_rel_ret.c m_gm_map.c m_Rprec.c m_recip_rank.c m_bpref.c \
	m_iprec_at_recall.c m_recall.c m_Rprec_mult.c m_utility.c m_11pt_avg.c \
        m_ndcg.c m_ndcg_cut.c m_Rndcg.c m_ndcg_rel.c \
	m_binG.c m_G.c \
        m_rel_P.c m_success.c m_infap.c m_map_cut.c \
	m_gm_bpref.c m_runid.c m_relstring.c \
        m_set_P.c m_set_recall.c m_set_rel_P.c m_set_map.c m_set_F.c \
        m_num_nonrel_judged_ret.c \
	m_prefs_num_prefs_poss.c m_prefs_num_prefs_ful.c \
        m_prefs_num_prefs_ful_ret.c\
	m_prefs_simp.c m_prefs_pair.c m_prefs_avgjg.c m_prefs_avgjg_Rnonrel.c \
	m_prefs_simp_ret.c m_prefs_pair_ret.c m_prefs_avgjg_ret.c\
        m_prefs_avgjg_Rnonrel_ret.c \
	m_prefs_simp_imp.c m_prefs_pair_imp.c m_prefs_avgjg_imp.c\
        m_map_avgjg.c m_Rprec_mult_avgjg.c m_P_avgjg.c m_yaap.c

SRCS = $(TOP_SRCS) $(FORMAT_SRCS) $(MEAS_SRCS)

SRCH = common.h trec_eval.h sysfunc.h functions.h trec_format.h

SRCOTHER = README Makefile test bpref_bug CHANGELOG

trec_eval: $(SRCS) Makefile $(SRCH)
	$(CC) $(CFLAGS)  -o trec_eval $(SRCS) -lm

install: $(BIN)/trec_eval

quicktest: trec_eval
	./trec_eval test/qrels.test test/results.test | diff - test/out.test
	./trec_eval -m all_trec test/qrels.test test/results.test | diff - test/out.test.a
	./trec_eval -m all_trec -q test/qrels.test test/results.test | diff - test/out.test.aq
	./trec_eval -m all_trec -q -c test/qrels.test test/results.trunc | diff - test/out.test.aqc
	./trec_eval -m all_trec -q -c -M100 test/qrels.test test/results.trunc | diff - test/out.test.aqcM
	./trec_eval -m all_trec -mrelstring.20 -q -l2 test/qrels.rel_level test/results.test | diff - test/out.test.aql
	./trec_eval -m all_prefs -q -R prefs test/prefs.test test/prefs.results.test | diff - test/out.test.prefs
	./trec_eval -m all_prefs -q -R qrels_prefs test/qrels.test test/results.test | diff - test/out.test.qrels_prefs
	./trec_eval -m qrels_jg -q -R qrels_jg  test/qrels.123 test/results.test | diff - test/out.test.qrels_jg
	./trec_eval -q -miprec_at_recall..10,.20,.25,.75,.50 -m P.5,7,3 -m recall.20,2000 -m Rprec_mult.5.0,0.2,0.35 -mutility.2,-1,0,0 -m 11pt_avg..25,.5,.75 -mndcg.1=3,2=9,4=4.5 -mndcg_cut.10,20,23.4 -msuccess.2,5,20 test/qrels.test test/results.test | diff - test/out.test.meas_params
	./trec_eval -q -m all_trec -Z test/zscores_file test/qrels.test test/results.test | diff - test/out.test.aqZ
	/bin/echo "Test succeeeded"

longtest: trec_eval
	/bin/rm -rf test.long; mkdir test.long
	./trec_eval test/qrels.test test/results.test > test.long/out.test
	./trec_eval -m all_trec test/qrels.test test/results.test > test.long/out.test.a
	./trec_eval -m all_trec -q test/qrels.test test/results.test > test.long/out.test.aq
	./trec_eval -m all_trec -q -c test/qrels.test test/results.trunc > test.long/out.test.aqc
	./trec_eval -m all_trec -q -c -M100 test/qrels.test test/results.trunc > test.long/out.test.aqcM
	./trec_eval -m all_trec -mrelstring.20 -q -l2 test/qrels.rel_level test/results.test > test.long/out.test.aql
	./trec_eval -m all_prefs -q -R prefs test/prefs.test test/prefs.results.test > test.long/out.test.prefs
	./trec_eval -m all_prefs -q -R qrels_prefs test/qrels.test test/results.test > test.long/out.test.qrels_prefs
	./trec_eval -m qrels_jg -q -R qrels_jg  test/qrels.123 test/results.test > test.long/out.test.qrels_jg
	./trec_eval -q -miprec_at_recall..10,.20,.25,.75,.50 -m P.5,7,3 -m recall.20,2000 -m Rprec_mult.5.0,0.2,0.35 -mutility.2,-1,0,0 -m 11pt_avg..25,.5,.75 -mndcg.1=3,2=9,4=4.5 -mndcg_cut.10,20,23.4 -msuccess.2,5,20 test/qrels.test test/results.test > test.long/out.test.meas_params
	./trec_eval -q -m all_trec -Z test/zscores_file test/qrels.test test/results.test > test.long/out.test.aqZ
	diff test.long test

$(BIN)/trec_eval: trec_eval
	if [ -f $@ ]; then $(INSTALL) $@ $@.old; fi;
	$(INSTALL) trec_eval $@

##4##########################################################################
##5##########################################################################
#  All code below this line (except for automatically created dependencies)
#  is independent of this particular makefile, and should not be changed!
#############################################################################

#########################################################################
# Odds and ends                                                         #
#########################################################################
clean semiclean:
	/bin/rm -f *.o *.BAK *~ trec_eval trec_eval.*.tar out.trec_eval Makefile.bak

tar:
	-/bin/rm -rf ./trec_eval.$(VERSIONID)
	mkdir trec_eval.$(VERSIONID)
	cp -rp $(SRCOTHER) $(SRCS) $(SRCH) trec_eval.$(VERSIONID)
	tar cf - ./trec_eval.$(VERSIONID) > trec_eval.$(VERSIONID).tar

#########################################################################
# Determining program dependencies                                      #
#########################################################################
depend:
	grep '^#[ ]*include' *.c \
		| sed -e 's?:[^"]*"\([^"]*\)".*?: \$H/\1?' \
			-e '/</d' \
			-e '/functions.h/d' \
		        -e 's/\.c/.o/' \
		        -e 's/\.y/.o/' \
		        -e 's/\.l/.o/' \
		> makedep
	echo '/^# DO NOT DELETE THIS LINE/+2,$$d' >eddep
	echo '$$r makedep' >>eddep
	echo 'w' >>eddep
	cp Makefile Makefile.bak
	ed - Makefile < eddep
	/bin/rm eddep makedep
	echo '# DEPENDENCIES MUST END AT END OF FILE' >> Makefile
	echo '# IF YOU PUT STUFF HERE IT WILL GO AWAY' >> Makefile
	echo '# see make depend above' >> Makefile

# DO NOT DELETE THIS LINE -- make depend uses it
# DEPENDENCIES MUST END AT END OF FILE
# IF YOU PUT STUFF HERE IT WILL GO AWAY
# see make depend above
