/*
作者:ai00
email:qiucxnju@gmail.com
*/
#ifndef __PROCESS_H__
#define __PROCESS_H__
#include "themis.h"
#define MAXPBUF 					100
class process{/*进程管理类*/
	int pid;/*进程ID*/
	int in;/*从进程读取数据的管道*/
	FILE  *out;/*往进程写入数据的管道*/
	string dir;/*进程源文件地址*/
	string name;/*进程源文件名,强制为"program"*/
	struct itimerval start_timer, finish_timer;/*用于卡时的数据结构*/
	struct rusage usage;/*用于进程回收的数据结构*/
	int vm_peak, vm_limit;/*用于卡内存使用量*/
	int state;/*记录进程的一些状态(state&2)表示进程处于暂停状态(state&1)表示进程已经被创建*/
	int raw_get(char *s, int bufsize);/*从进程读取一次数据*/
	public:
	process();/*默认初始函数*/
	void stop();/*中止进程*/
	int start(int time, char *o, char *i, int bufsize);/*启动进程，启动异常返回false，否则返回true*/
	bool create(string s, int vm);/*创建以s为路径的进程*/
	bool create();/*创建以process::dir未路径的进程*/
	int communicate(int time, char *o, char *i, int bufsize);/*进行一次通信交互，返回错误号*/
	int get(int time, char *s, int bufsize);/*从进程读取一次数据，并卡时,返回错误号*/
	void print(char* s);/*向进程写入数据*/
	void init_timer(int time_limit);/*创建时钟中断*/
	void stop_timer();/*关闭时钟中断*/
	int check_vm();/*检查内存*/
};
void tle_kill(int s);/*时钟中断响应函数，超时关闭进程*/
#endif
