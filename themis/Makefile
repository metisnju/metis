#作者:ai00
#email:qiucxnju@gmail.com
CC = g++
CFLAGS = -static -I ./include
OBJS = process.o \
	themis.o
program: $(OBJS)
	$(CC) $(CFLAGS) -o themis $(OBJS) 
	make clean
-include *.d
other:
	make -C judge clean
	make -C judge program
	make -C ai0 clean
	make -C ai0 program
	make -C ai1 clean
	make -C ai1 program
clean: 
	rm -f $(OBJS)
cleanall:
	make clean
	rm -f themis
run:
	sudo ./themis player_num=2 
all:
	make other
	make
crun:
	make all
	make run
