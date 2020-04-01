import getopt
import sys

if __name__ == '__main__':
    OPTS, ARGS = getopt.getopt(sys.argv[1:], '', ['group-id=', 'artifact-id=', 'version=', 'port=', 'package-name=', 'project-path='])
    print(OPTS, ARGS)
