import getopt
import sys

if __name__ == '__main__':
    OPTS, ARGS = getopt.getopt(sys.argv[1:], '', ['group_id=', 'artifact_id=', 'version=', 'port=', 'package_name=', 'project_path='])
    print(OPTS, ARGS)
