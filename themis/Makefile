#作者:ai00
#email:qiucxnju@gmail.com
CC = g++
CFLAGS = -static -I ./include
OBJS = process.o \
	themis.o 
RM = process.o \
	themis.o \
	doc/Hecatonchires.aux \
	doc/Hecatonchires.log \
	doc/Hecatonchires.synctex.gz \
	doc/Hecatonchires.toc \
	themis.out
program: $(OBJS)
	$(CC) $(CFLAGS) -o themis.out $(OBJS) 

-include *.d
clean: 
	rm -f $(RM)
