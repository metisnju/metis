#ifndef __OTHELLO_AI_H__
#define __OTHELLO_AI_H__
#include "include/othello.h"
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
void othello_ai::init(int color, string s){
	o.init(color, s);
}
void othello_ai::move(int color, int x, int y){
	o.play(color, x, y);
}
pair<int, int> othello_ai::get(){
	vector<pair<int, int> > ans = o.allmove(o.mycolor);
	return ans[0];
}
#endif
