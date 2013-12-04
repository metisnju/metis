/*
作者:ai00
email:qiucxnju@gmail.com
*/
#include "include/themis.h"
using namespace std;
process judge, player[MAXPER];
int pl_num = MAXPER;/*记录玩家数*/
int judge_time = DEFUTE_TIME;/*judge的时间限制*/
int player_time = DEFUTE_TIME;/*玩家的时间限制*/
int judge_vm = DEFUTE_VM;/*judge的内存限制*/
int player_vm = DEFUTE_VM;/*晚间的内存限制*/
char buf[MAXBUF + 100];/*数据缓冲*/
FILE *f;/*文件指针，用于DORA自己DEBUG*/

void my_exit(bool error,int errnum){
	/*关闭所有的子进程*/
	judge.stop();
	for (int i = 0; i < pl_num; ++i)
		player[i].stop();
	if (error)
		printf("judge_error, ERRORID = %d\n", errnum);
	fclose(f);
}

int main(int argc, char *argv[]){
	//cout<<ORIG_REAX<<endl;
	//return 0;
	/*创建DEBUG文件*/
	f = fopen("debug.txt", "w");

	/*初始化参数*/
	for (int i = 0; i < argc; ++i){
		if (strncmp(argv[i], "player_num=", 11) == 0)
			sscanf(argv[i] + 11, "%d", &pl_num);
		if (strncmp(argv[i], "judge_time=", 11) == 0)
			sscanf(argv[i] + 11, "%d", &judge_time);
		if (strncmp(argv[i], "player_time=", 12) == 0)
			sscanf(argv[i] + 12, "%d", &player_time);
	}

	/*创建所有用于评测的进程*/
	fprintf(f, "will create\n");
	judge.create("judge/", judge_vm);
	for (int i = 0; i < pl_num; ++i){
		strcpy(buf, "ai0/");
		buf[2] = i + '0';
		fprintf(f, "%s %d\n", buf, player[i].create(buf, player_vm));
		//fprintf(f, "%s %d\n", buf, player[i].state);
	}
	fprintf(f, "ok\n");

	/*如果judge无法初始化，错误退出*/
	int err = 0;
	fprintf(f, "starting\n");
	strcpy(buf, "null");
	if ((err = judge.start(judge_time, buf, buf, MAXBUF)) != ERR_NOERR){
		my_exit(true, err);
		return 0;
	}
	fprintf(f, "chu shi wanchen\n");

	/*循环向judge询问操作*/
	while(true){
		/*如果无法从judge得到回复，错误退出*/
		fprintf(f, "get\n");
		if ((err = judge.get(judge_time, buf, MAXBUF)) != ERR_NOERR){
			my_exit(true, err);
			return 0;
		}
		fprintf(f, "%d %s\n", err, buf);
		//cout<<buf<<endl;
		
		/*如果judge要求DEBUG*/
		if (strncmp(buf, "DEBU", 4) == 0){
			printf("%s\n", buf + 5);
			strcpy(buf, "DEBUOK");
			judge.print(buf);
		}
		
		/*如果judge要求退出*/
		if (strncmp(buf, "EXIT", 4) == 0){
			my_exit(false);
			return 0;
		}

		/*如果judge要求关闭某个进程*/
		if (strncmp(buf, "STOP", 4) == 0){
			int i;
			sscanf(buf + 5, "%d", &i);
			player[i].create();
			strcpy(buf, "STOPOK");
			judge.print(buf);
		}

		/*如果judge要求启动某个进程*/
		if (strncmp(buf, "STAR", 4) == 0){
			fprintf(f, "_______________find STAR\n");
			int i = buf[5] - '0';
			err = player[i].start(player_time, buf + 7, buf, MAXBUF);
			fprintf(f, "%d\n", err);
			if (err == ERR_NOERR) strcpy(buf, "SSUC");
			else sprintf(buf, "SERR %d", err);
			judge.print(buf);
		}

		/*如果进程要求和某个进程通信*/
		if (strncmp(buf, "COMM", 4) == 0){
			int i = buf[5] - '0';
			//cout<<buf<<endl;
			err = player[i].communicate(player_time, buf + 7, buf + 5, MAXBUF);
			//cout<<OK<<endl;
			if (err == ERR_NOERR) strncpy(buf, "CSUC", 4);
			else sprintf(buf, "CERR %d", err);
			//cout<<"++++++++++++"<<buf<<endl;
			judge.print(buf);
		}
	}
}
