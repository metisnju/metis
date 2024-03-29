#ifndef __PRINTER_H__
#define __PRINTER_H__
#include "const.h"
#include <cstdio>
#include <cstdarg>
#include <iostream>
#include <cstdlib>
#include <cstring>
using namespace std;
class printer{
	char buf[MAXBUF + 100];
	public:
	char *data;
	void check(char *data);
	printer();
	int com(int turn, char *out);
	void debug(const char *format, ...);
	void end();
	int init(int turn, char *out);
	int com(int turn);
	int init(int turn);
};
#endif
