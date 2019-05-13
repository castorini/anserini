    
## Python Collections Wrapper

### Example usage 

```
from collection.collection import *
```

```
# Fetching collection given class and path to directory
collection = Collection('TrecCollection', '/path/to/disk45')

# Get file segment in collection
fs = next(collection.segments)

# Get doc in file segment
doc = next(fs)

# Document id
doc.id

# Raw document contents
doc.contents

# Whether document is indexable
doc.indexable
```

### To iterate over collection and process document:  

```
collection = Collection(collection_class, input_path)

for (i, fs) in enumerate(collection.segments):
	for (i, doc) in enumerate(fs):
		foo(doc)
```