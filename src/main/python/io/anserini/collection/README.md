    
## Python Collections Wrapper

### Example usage 

```
import sys
sys.path += ['src/main/python/io/anserini']
from collection import pycollection, pygenerator
```

```
# Fetching collection given class and path to directory
collection = pycollection.Collection('TrecCollection', '/path/to/disk45')

# Get file segment in collection
fs = next(collection.segments)

# Get doc in file segment
doc = next(fs)

# Document id
doc.id

# Raw document contents
doc.contents
```

```
# Fetching Lucene document generator given generator class
generator = pygenerator.Generator('JsoupGenerator')
```

### To iterate over collection and process document  

```
collection = pycollection.Collection(collection_class, input_path)

for (i, fs) in enumerate(collection.segments):
    for (i, doc) in enumerate(fs):
        # foo(doc)
        # for example:

        parsed_doc = generator.generator.createDocument(doc.document)
        id = parsed_doc.get('id')               # FIELD_ID
        raw = parsed_doc.get('raw')             # FIELD_RAW
        contents = parsed_doc.get('contents')   # FIELD_BODY
```