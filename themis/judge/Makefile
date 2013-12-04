CC = g++
CFLAGS = -static -I ./include
OBJS = sys/printer.o \
	include/othello.o \
	main.o \
	judge.o
program: $(OBJS)
	$(CC) $(CFLAGS) -o program $(OBJS)
	make clean
-include *.d
clean: 
	rm -f $(OBJS)
cleanall:
	make clean
	rm -f program

