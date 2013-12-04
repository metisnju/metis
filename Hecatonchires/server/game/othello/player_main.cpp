#include "othello_ai.cpp"
#include <iostream>
#include <cstdio>
using namespace std;
othello_ai ai;
void init(char *s){
	ai.init(s[0] - '0', s + 1);
	printf("SUCC\n");
}
void run(){
	char op[100];
	int color, x, y;
	while (true){
		scanf("%s", op);
		if (strncmp(op, "GET", 3) == 0){
			while('\n' != getchar());
			pair<int, int> move = ai.get();
			printf("%d %d\n", move.first, move.second);
			fflush(stdout);
		}else{
			scanf("%d%d%d", &color, &x, &y);
			while('\n' != getchar());
			ai.move(color, x, y);
			printf("SUCC\n");
			fflush(stdout);
		}
	}
}
int main(int argc, char *argv[]){
	init(argv[1]);
	fflush(stdout);
	run();
}
