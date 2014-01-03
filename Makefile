#作者:ai00
#email:qiucxnju@gmail.com
compile:
	make -C othello
	make -C themis
	cp ./othello/othello/Makefile /usr/local/apache/htdocs/upload/1/Makefile
	cp ./othello/judge/program /usr/local/apache/htdocs/upload/1/judge.out
	cp ./themis/themis.out /usr/local/apache/htdocs/upload/themis.out

runms:
	make -C Hecatonchires runms
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

