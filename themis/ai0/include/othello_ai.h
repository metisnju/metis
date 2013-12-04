#ifndef __OTHELLO_AI_H__
#define __OTHELLO_AI_H__
#include "othello.h"
#include <cstdlib>
#include <iostream>
using namespace std;
class othello_ai{
	othello o;
	public:
	void init(int color, string s);
	void move(int color, int x, int y);
	pair<int, int> get();
};
#endif
