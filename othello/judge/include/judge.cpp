#include "judge.h"
judge::judge(){
	o.init();
}
void judge::init(const char *s){
}
void judge::run(){
	int turn = 0;
	state[1] = state[0] = ERR_NOTSTART;
	while (true){
		for (int i = 0; i < 2; ++i)
			if ((state[i] != ERR_NOERR)&&(player_init(i) != ERR_NOERR)){
				p.debug("INITERR %d ERRORID = %d\n", i, state[i]);
				judge_exit(1 - i);
				return;
			}
		if (o.canmove(turn + 1)) judge_com(turn);
		else if (!o.canmove(2 - turn)){
			int x = o.count(1);
			int y = o.count(2);
			p.debug("REPORT %d %d\n", x, y);
			judge_exit((x >= y)?0:1);
		}
		turn = 1 - turn;
	}
}



int judge::player_init(int id){
	string s = o.tostring();
	sprintf(p.data, "%d%s", id + 1, s.c_str());
	state[id] = p.init(id);
	p.debug("START %d ERRID = %d\n", id, state[id]);
	return state[id];
	
}

void judge::judge_com(int turn){
	sprintf(p.data, "GET");
	state[turn]= p.com(turn);
	if (state[turn] == ERR_NOERR){
		int x, y;
		sscanf(p.data, "%d %d", &x, &y);
		o.play(turn + 1, x, y);
		p.debug("MOVE %d %d %d\n", turn, x, y);
		fflush(stdout);
		for (int i = 0; i < 2; ++i){
			sprintf(p.data, "MOVE %d %d %d", 1 + turn, x, y);
			state[i] = p.com(i);
			if (state[i] != ERR_NOERR) p.debug("RUNNTIMEERR %d ERRORID = %d\n", i, state[i]);
		}
	}else{
		p.debug("RUNNTIMEERROR %d ERRORID = %d\n", turn, state[turn]);
		int x, y;
		o.play(turn + 1, x, y);
		p.debug("MOVE %d %d %d\n", turn, x, y);
		sprintf(p.data, "MOVE %d %d %d", 1 + turn, x, y);
		state[1 - turn] = p.com(1- turn);
		if (state[1 - turn] != ERR_NOERR) p.debug("RUNNTIMEERR %d ERRORID = %d\n", 1 - turn, state[1 - turn]);
	}
}

void judge::judge_exit(int id){
	p.debug("WINNER %d\n", id);
	p.debug("EXIT\n");
	p.end();
}
