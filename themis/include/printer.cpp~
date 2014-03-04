#include "printer.h"
printer::printer(){
	data = buf + 5;
}
void printer::debug(const char *format, ...){
	printf("DEBU ");
	va_list ap;
	va_start(ap, format);
	vprintf(format, ap);
	va_end(ap);
	fflush(stdout);
	while ('\n' != getchar());
}
int printer::init(int turn){
	return init(turn, data);
}
int printer::init(int turn, char *out){
	check(out);
	int ret;
	printf("STAR %d %s\n", turn, data);
	fflush(stdout);
	scanf("%[^\n]", buf);
	while ('\n' != getchar());
	if (strncmp(buf, "SSUC", 4) == 0) ret = ERR_NOERR;
	else sscanf(data, "%d", &ret); 
	return ret;
}
void printer::check(char *out){
	for (int i = 0; out[i] != '\0'; ++i)
		if (out[i] == '\n'){
			out[i] = '\0';
			break;
		}
}

int printer::com(int turn){
	return com(turn, data);
}
int printer::com(int turn, char *out){
	check(out);
	int ret;
	printf("COMM %d %s\n", turn, out);
	fflush(stdout);
	scanf("%[^\n]", buf);
	while ('\n' != getchar());
	if (strncmp(buf, "CSUC", 4) == 0) ret = ERR_NOERR;
	else sscanf(data, "%d", &ret); 
	return ret;
}

void printer::end(){
	printf("EXIT\n");
	fflush(stdout);
}
