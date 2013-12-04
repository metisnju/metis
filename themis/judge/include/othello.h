#ifndef __OTHELLO_H__
#define __OTHELLO_H__
#include <cstring>
#include <string>
#include <vector>
using namespace std;
class othello{
	static const int direction[8][2];
	static const int MAXN;
	int map[8][8];
	void set(int color, int x, int y);
	public:
	int mycolor;
	void init();
	void init(int color, string s);
	bool canmove(int color, int x, int y);
	bool is(int color, int x, int y);
	bool canmove(int color);
	int count(int color);
	vector<pair<int, int> > allmove(int color);
	bool play(int turn, int &x, int &y);
	string tostring();
};

#endif
