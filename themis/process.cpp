/*
作者:ai00
email:qiucxnju@gmail.com
*/
#include "include/process.h"
#include "include/syscalls.h"
process *now = NULL;/*指向当前正被执行的进程*/
char pbuf[MAXPBUF];
void tle_kill(int s){
	if (now != NULL) now->stop();
	now = NULL;
}
void process::init_timer(int time_limit){
	/*记录应该被中断的进程*/
	now = this;

	/*创建中断响应函数*/
	struct sigaction act;
	act.sa_handler = tle_kill;
	act.sa_flags = 0;
	sigemptyset(&act.sa_mask);
	sigaction(SIGALRM, &act, NULL);
	sigaction(SIGPROF, &act, NULL);	

	/*创建一次性时钟中断*/
	start_timer.it_interval.tv_sec = 0;
	start_timer.it_interval.tv_usec = 0;
	start_timer.it_value.tv_sec = time_limit / 1000;
	start_timer.it_value.tv_usec = time_limit % 1000 * 1000;
	setitimer(ITIMER_REAL, &start_timer, &finish_timer);
}
process::process(){
	state = 0;
	in = 0;
	out = NULL;
}
int process::start(int time, char *o, char *i, int bufsize){
	return communicate(time, o, i, bufsize);
}

int process::communicate(int time, char *o, char *i, int bufsize){
	/*当进程未被创建时，发生异常*/
	if (!(state&1)) return ERR_PROCESS_NOEXIST;

	/*写入数据*/
	print(o);
	return get(time, i, bufsize);
}
void process::print(char * s){
	fprintf(out, "%s\n", s);
	fflush(out);
}
int process::raw_get(char * s, int bufsize){
	int orig_eax, status, sig;

	/*若进程处于挂起状态，恢复进程运行*/
	if (state&2) 
		ptrace(PTRACE_SYSCALL, pid, NULL, NULL);

	/*依次处理每一个中断*/
	while (1) {
		/*等待中断*/
		wait4(pid, &status, WUNTRACED, &usage);

		/*当子进程遇到一个不能处理的信号时返回true*/
		if (WIFSIGNALED(status)) {
			return ERR_UNHANDLED_INTERUPT;
		}
		
		/*当子进程收到停止信号时为true*/
		if (!WIFSTOPPED(status)) {
			return ERR_SHUTDOWN;
		}
		
		/*取得引发子进程暂停的信号代码*/
		sig = WSTOPSIG(status);

		/*检查这个信号是否是通过traceme后wait得到的*/
		if (sig != SIGTRAP) {
			switch(sig) {
				case SIGFPE: 
				case SIGSEGV: 
				case SIGXFSZ: 
				case SIGILL: 
				case SIGKILL: 
				case SIGALRM: 
				case SIGPROF: return ERR_SHUTDOWN;
			}
			ptrace(PTRACE_SYSCALL, pid, NULL, sig);
			continue;
		}

		/*获取中断号*/
		orig_eax = ptrace(PTRACE_PEEKUSER, pid, ORIG_REAX, NULL);

		/*若中断是不合法的，异常，当然，第一次不合法的中断
		是允许的，其发生在execl的时候*/
		//cout<<orig_eax<<endl;
		if ((orig_eax < CALLNUM)&&disabled_syscall[orig_eax]){
			if (state & 4) return ERR_RUNTIME_ERR;
			state |= 4;
		}

		/*检查跟内存有关的中断是否导致内存超限制*/
		if (orig_eax == 192 || orig_eax == 90 || orig_eax == 45) {
			if (ERR_VM_LIMIT == check_vm()) return ERR_VM_LIMIT;
		}

		/*从管道读取进程输出的东西，当读完一行后，运行成功*/
		int len = read(in, s, bufsize);
		if (len > 0){
			if (len == bufsize) return ERR_OUTPUT_LIMIT;
			for (int i = 0; i < len; ++i, --bufsize, ++s)
				if ('\n' == *s){
					/*这里把进程标记为暂停，防止恶性竞争*/
					state |= 2;
					*s = '\0';
					return ERR_NOERR;
				}
		}

		/*进程没有输出数据，回复进程，让其继续运行*/
		ptrace(PTRACE_SYSCALL, pid, NULL, NULL);
	}
}
int process::get(int time, char *s, int bufsize){
	/*创建时钟中断*/
	init_timer(time);

	/*读取数据*/
	int ret = raw_get(s, bufsize);

	/*关闭时钟中断*/
	init_timer(0);
	/*如果输入异常，停止进程*/
	//cout<<vm_peak<<endl;
	if (ret == ERR_NOERR) ret = check_vm();
	if (ret != ERR_NOERR) stop();
	//cout<<vm_peak<<endl;
	if(!(state&1))	create();
	return ret;
}


void process::stop(){
	if (state){
		close(in);
		fclose(out);
		kill(pid, SIGKILL);
		state = 0;
		pid = 0;
	}
}

bool process::create(string s, int vm){
	dir = s;
	vm_limit = vm;
	name = dir + "program";
	return create();
}
bool process::create(){
	if (state&1){
		stop();
	}
	int pipe_fdr[2];
	int pipe_fdw[2];
	/*设置内存检测*/
	vm_peak = 0;

	/*打开管道*/

	/*子进程利用这个管道读, 父进程利用这个管道写*/
	if (pipe(pipe_fdr) == -1)
		return false;

	/*子进程利用这个管道写,父进程利用这个管道读:*/
	if (pipe2(pipe_fdw, O_NONBLOCK) == -1)
		return false;

	/*创建子进程*/
	pid = fork();
	if (pid == 0){/*子进程操作*/

		/*把管道设置为stdin和stdout*/
		if (dup2(pipe_fdr[0], 0) == -1)
			return false;
		close(pipe_fdr[0]);
		close(pipe_fdr[1]);
		if (dup2(pipe_fdw[1], 1) == -1)
			return false;
		close(pipe_fdw[0]);
		close(pipe_fdw[1]);

		/*更改工作目录和根目录*/
		chdir(dir.c_str());
		chroot(dir.c_str());

		/*等待初始化*/
		char buf[100];
		scanf("%s", buf);
		while('\n' != getchar());

		/*设置跟踪*/
		ptrace(PTRACE_TRACEME, 0, NULL, NULL);
		
		/*执行程序*/
		execl("program", "program", buf, NULL);

	}else{/*父进程操作*/ 

		/*设置管道*/
		close(pipe_fdr[0]); 
		close(pipe_fdw[1]);
		out = fdopen(pipe_fdr[1], "w"); 
		in = pipe_fdw[0];
		return state = 1;
	}
}
int process::check_vm(){
	sprintf(pbuf, "/proc/%d/status", pid);
	//cout<<pbuf<<endl;
	FILE *fp = fopen(pbuf, "r");
	if (fp == NULL) return -1;
	while (!feof(fp)) {
		fgets(pbuf, 100, fp);
		if (strncmp(pbuf, "VmPeak:", 7) == 0) {
			break;
		}
	}
	sscanf(pbuf + 7, "%d", &vm_peak);
	fclose(fp);
	return (vm_peak <= vm_limit)?ERR_NOERR:ERR_VM_LIMIT;
}
