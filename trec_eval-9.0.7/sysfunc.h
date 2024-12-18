/* 
   Copyright (c) 2008 - Chris Buckley. 

   Permission is granted for use and modification of this file for
   research, non-commercial purposes. 
*/
#ifndef SYSFUNCH
#define SYSFUNCH
/* Declarations of major functions within standard C libraries */
/* Once all of the major systems get their act together (and I follow
   suit!), this file should just include system header files from 
   /usr/include.  Until then... */

#include <unistd.h>
#include <limits.h>
#include <ctype.h>
#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include <memory.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <sys/time.h>
#include <sys/mman.h>

#include <getopt.h>

/* see http://stackoverflow.com/questions/33058014/trec-eval-make-error-using-cygwin/34927338 */
#ifdef __CYGWIN__
#undef log2
#endif

/* For time being, define Berkeley constructs in terms of SVR4 constructs*/
#ifndef bzero
#define bzero(dest,len)      memset(dest,'\0',len)
#endif
#ifndef bcopy
#define bcopy(source,dest,len)   memcpy(dest,source,len)
#endif
#define srandom(seed)        srand(seed)
#define random()             rand()

/* ANSI should give us an offsetof suitable for the implementation;
 * otherwise, try a non-portable but commonly supported definition
 */
#ifdef __STDC__	
#include <stddef.h>
#endif
#ifndef offsetof
#define offsetof(type, member) ((size_t) \
	((char *)&((type*)0)->member - (char *)(type *)0))
#endif

#endif /* SYSFUNCH */
