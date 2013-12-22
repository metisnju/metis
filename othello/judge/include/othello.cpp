#include "othello.h"
const int othello::MAXN = 8;
const int othello::direction[8][2] = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

string othello::tostring(){
	string s = "";
	for (int i = 0; i < MAXN; ++i)
		for (int j = 0; j < MAXN; ++j){
			s += map[i][j] + '0';
		}
	return s;
}

int othello::count(int color){
	int ret = 0;
	for (int i = 0; i < MAXN; ++i)
		for (int j = 0; j < MAXN; ++j)
			if (map[i][j] == color) ++ret;
	return ret;
}

void othello::init(){
	mycolor = 0;
	memset(map, 0, sizeof(map));
	map[3][3] = map[4][4] = 1;
	map[3][4] = map[4][3] = 2;
}

void othello::init(int color, string s){
	mycolor = color;
	for (int i = 0; i < MAXN * MAXN; ++i)
		map[i / MAXN][i % MAXN] = s[i] - '0';
}

void othello::set(int color, int x, int y){
	map[x][y] = color;
	for (int i = 0; i < 8; ++i){
		int sx = x, sy = y;
		while (is(3 - color, sx + direction[i][0], sy + direction[i][1])){
			sx += direction[i][0];
			sy += direction[i][1];
		}
		if (is(color, sx + direction[i][0], sy + direction[i][1])){
			sx = x, sy = y;
			while(is(3 - color, sx + direction[i][0], sy + direction[i][1])){
				sx += direction[i][0];
				sy += direction[i][1];
				map[sx][sy] = color;
			}
		}
	}
}
bool othello::canmove(int color, int x, int y){
	if (x < 0) return false;
	if (y >= MAXN) return false;
	if (x >= MAXN) return false;
	if (y < 0) return false;
	if (map[x][y]) return false;
	for (int i = 0; i < 8; ++i){
		int sx = x, sy = y;
		while(is(3 - color, sx + direction[i][0], sy + direction[i][1])){
			sx += direction[i][0];
			sy += direction[i][1];
		}
		if ((sx == x)&&(sy == y)) continue;
		if (is(color, sx + direction[i][0], sy + direction[i][1])) return true;
	}
	return false;
}

bool othello::is(int color, int x, int y){
	if (x < 0) return false;
	if (y < 0) return false;
	if (x >= MAXN) return false;
	if (y >= MAXN) return false;
	return color == map[x][y];
}

bool othello::canmove(int color){
	for (int i = 0; i < MAXN; ++i)
		for (int j = 0; j < MAXN; ++j)
				if (canmove(color, i, j)) return true;
	return false;
}

vector<pair<int, int> > othello::allmove(int color){
	vector<pair<int, int> > moves;
	for (int i = 0; i < MAXN; ++i)
		for (int j = 0; j < MAXN; ++j)
			if (canmove(color, i, j)) moves.push_back(make_pair(i, j));
	return moves;
}

bool othello::play(int turn, int &x, int &y){
	if (canmove(turn, x, y)){
		set(turn, x, y);
		return true;
	}
	for (x = 0; x < MAXN; ++x)
		for (y = 0; y < MAXN; ++y)
			if (canmove(turn, x, y)){
				set(turn, x, y);
				return true;
			}
	return false;
}
