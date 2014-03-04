#ifndef __JUDGE_H__
#define __JUDGE_H__
#include "othello.h"
#include <cstdio>
#include <cstdarg>
#include <iostream>
#include <cstdlib>
#include "printer.h"
using namespace std;
class judge{
	printer p;
	othello o;
	int state[2];
	public:
	judge();
	int player_init(int id);
	void judge_com(int id);
	void judge_exit(int id);
	void run();
	void init(const char *s);
};
#endif
