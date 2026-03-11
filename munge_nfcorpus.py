import json

with open('collections/nfcorpus/pyserini-corpus/corpus.jsonl', 'w') as out:
    with open('collections/nfcorpus/corpus.jsonl', 'r') as f:
        for line in f:
            l = json.loads(line)
            s = json.dumps({'id': l['_id'], 'contents': l['title'] + ' ' + l['text']})
            out.write(s + '\n')
