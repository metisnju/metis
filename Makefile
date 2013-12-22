#作者:ai00
#email:qiucxnju@gmail.com
compile:
	make -C othello
clean:
	make -C themis clean
	make -C Hecatonchires clean
	make -C othello clean
	git add themis
	git add Hecatonchires
	git add lib
	git add othello
	git add git
	git add Makefile
