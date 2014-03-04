
#include <cstdio>
#include "othello.h"
class othello_ai{
	othello o;
	public:
	void init(int color, string s);
	void move(int color, int x, int y);
	pair<int, int> get();
	int n;
};
void othello_ai::init(int color, string s){
	n = 0;
	o.init(color, s);
}
void othello_ai::move(int color, int x, int y){
	fprintf(stderr, "hello %d\n", ++n);
	o.play(color, x, y);
}
pair<int, int> othello_ai::get(){
	vector<pair<int, int> > ans = o.allmove(o.mycolor);
	return ans[0];
}
