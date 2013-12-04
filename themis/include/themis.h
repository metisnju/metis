/*
作者:ai00
email:qiucxnju@gmail.com
*/
#include <unistd.h>
#include <fcntl.h>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/wait.h>
#include <sys/syscall.h>
#include <asm/ptrace-abi.h>
#include <sys/reg.h>


#include <sys/reg.h>
#include <signal.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
/*
#include <sys/types.h>  
#include <sys/wait.h>  
#include <sys/ptrace.h>  
#include <sys/reg.h>  
#include <sys/ptrace.h>
#include <sys/time.h>
#include <sys/syscall.h>
#include <sys/reg.h>
#include <unistd.h>  
#include <signal.h>

#include <cstring>
#include <cstdio>
#include <cstdlib>
*/
#include <iostream>
using namespace std;
#include "process.h"
#include "const.h"
#ifndef __THEMIS_H__
#define __THEMIS_H__
#define MAXPER		10 /*玩家数上限*/
#define MAXBUF  	4096/*缓冲长度*/
#define DEFUTE_TIME	1000/*默认时间限制*/
#define DEFUTE_VM	64000/*默认内存限制*/
void my_exit(bool error,int errid = 0);/*程序结束，error为true表示发生的judge_error*/

#endif
