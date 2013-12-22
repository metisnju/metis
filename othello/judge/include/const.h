/*
作者:ai00
email:qiucxnju@gmail.com
*/
#ifndef __ERR_H__
#define __ERR_H__
#define ERR_NOERR					0/*没有错误*/
#define ERR_NOTSTART				1/*进程未启动*/
#define ERR_UNHANDLED_INTERUPT		2/*未定义处理中断*/
#define ERR_SHUTDOWN				3/*超时关闭*/
#define ERR_RUNTIME_ERR				4/*运行时错误*/
#define ERR_VM_LIMIT				5/*内存超界*/
#define ERR_OUTPUT_LIMIT			6/*输出超界*/
#define MAXPER						10 /*玩家数上限*/
#define MAXBUF  					4096/*缓冲长度*/
#define DEFUTE_TIME					1000/*默认时间限制*/
#define DEFUTE_VM					64000/*默认内存限制*/
#endif
