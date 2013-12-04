import os
path = './src'
def tranf(sordir, desdir):
	os.system('iconv -f gbk -t utf-8 -o ' + desdir + ' ' + sordir); 
def tran(sordir, desdir):
	os.system('mkdir ' + desdir)
	files = os.listdir(sordir)
	for f in files:
		if (os.path.isdir(sordir + '/' + f)):
			if (f[0] == '.'):
				pass
			else:
				tran(sordir + '/' + f, desdir + '/' + f)
		if (os.path.isfile(sordir + '/' + f)):
			tranf(sordir + '/' + f, desdir + '/' + f)
sordir = "./src"
desdir = "./nsrc"
if __name__=='__main__':
	tran(sordir, desdir)
