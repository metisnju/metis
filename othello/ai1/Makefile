CC = g++
CFLAGS = -static -I ./include
OBJS = include/othello.o \
	include/othello_ai.o \
	main.o
program: $(OBJS)
	$(CC) $(CFLAGS) -o program $(OBJS)
-include *.d
clean: 
	rm -f $(OBJS)
	rm -f program

