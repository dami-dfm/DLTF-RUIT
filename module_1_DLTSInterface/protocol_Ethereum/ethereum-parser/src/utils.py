
def read_headers(path: str):
    with open(path) as f:
        first_line = f.readline()
        raw_headers = first_line.split(',')
        headers = []
        for header in raw_headers:
            # Remove neo4j specific header syntax
            headers.append(header.split(':')[0])
        return headers
