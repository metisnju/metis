#include "include/othello_ai.h"
#include <iostream>
#include <cstdio>
using namespace std;
othello_ai ai;
void init(char *s){
	//FILE *err;
	//err = fopen("out.txt", "a");
	//fprintf(err, "ai0\n");
	//fprintf(err, "%s\n", s);
	//fclose(err);
	//string map;
	//cin>>color>>map;
	ai.init(s[0] - '0', s + 1);
	//printf("SUCC\n");
	//fflush(stdout);
	//err = fopen("out.txt", "a");
	//fprintf(err, "ai0\n");
	//fprintf(err, "%s\n", s);
	//fclose(err);
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
	printf("SUCC\n");
	fflush(stdout);
	run();
}
