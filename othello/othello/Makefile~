CC = g++
CFLAGS = -static -I ./include
-include *.d
all:
	cp p0.cpp othello_ai.cpp
	g++ -c -o include/othello.o include/othello.cpp
	g++ -c -o main.o player_main.cpp
	g++ -static -I ./include -o program include/othello.o main.o
	mkdir ai0
	rm othello_ai.cpp
	cp program ai0/program
	rm include/othello.o
	rm main.o
	rm program

	cp p1.cpp othello_ai.cpp
	g++ -c -o include/othello.o include/othello.cpp
	g++ -c -o main.o player_main.cpp
	g++ -static -I ./include -o program include/othello.o main.o
	mkdir ai1
	rm othello_ai.cpp
	cp program ai1/program
	rm include/othello.o
	rm main.o
	rm program

	g++    -c -o themis/printer.o themis/printer.cpp
	g++    -c -o include/othello.o include/othello.cpp
	g++    -c -o main.o judge_main.cpp
	g++    -c -o judge.o judge.cpp
	g++ -static -I ./include -o program themis/printer.o include/othello.o main.o judge.o
	rm themis/printer.o
	rm include/othello.o
	rm main.o
	rm judge.o
	mkdir judge
	cp program judge/program
	rm program

	./themis.out player_num=2 >output
clean:
	rm -rf ai0
	rm -rf ai1
	rm -rf judge
compilejudge:
	g++    -c -o themis/printer.o themis/printer.cpp
	g++    -c -o include/othello.o include/othello.cpp
	g++    -c -o main.o judge_main.cpp
	g++    -c -o judge.o judge.cpp
	g++ -static -I ./include -o program themis/printer.o include/othello.o main.o judge.o
	rm themis/printer.o
	rm include/othello.o
	rm main.o
	rm judge.o
compileplayer:
	g++ -c -o include/othello.o include/othello.cpp
	g++ -c -o main.o player_main.cpp
	g++ -static -I ./include -o program include/othello.o main.o
	rm include/othello.o
	rm main.o
test:
	sudo ./themis.out player_num=2 >output
