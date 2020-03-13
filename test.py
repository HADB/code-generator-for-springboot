import getopt
import sys

if __name__ == '__main__':
    OPTS, ARGS = getopt.getopt(sys.argv[1:], 'ip:', ['init', 'port='])
    print(OPTS, ARGS)
