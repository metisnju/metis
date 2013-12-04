#include "othello_ai.h"
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
