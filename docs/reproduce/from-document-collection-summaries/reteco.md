# ⚗️ Anserini: Reproductions from Document Collections

[<< Main Landing Page for Reproductions from Document Collections](../../ref-reproduce-from-document-collections.md)

Anserini supports end-to-end reproduction experiments on various standard IR test collections out of the box.
Each of these experiments starts from the raw document collection, builds the necessary index, performs retrieval runs, and generates evaluation results.

## RETECO v1.1 Track 1a

RETECO is a reasoning-oriented retrieval benchmark described [here](https://datascienceuibk.github.io/RETECO/).
These regressions cover BM25 on the deduplicated v1.1 corpus for Track 1a train/dev, with one full domain index shared by both query sets.

Key:

+ BM25

| Corpus | BM25 |
|:---|:---:|
| Bitcoin | [🔑](../from-document-collection/reteco-v1.1-track1-bitcoin.md) |
| Cardano | [🔑](../from-document-collection/reteco-v1.1-track1-cardano.md) |
| Economics | [🔑](../from-document-collection/reteco-v1.1-track1-economics.md) |
| Genealogy | [🔑](../from-document-collection/reteco-v1.1-track1-genealogy.md) |
| History | [🔑](../from-document-collection/reteco-v1.1-track1-history.md) |
| HSM | [🔑](../from-document-collection/reteco-v1.1-track1-hsm.md) |
| IOTA | [🔑](../from-document-collection/reteco-v1.1-track1-iota.md) |
| Law | [🔑](../from-document-collection/reteco-v1.1-track1-law.md) |
| Monero | [🔑](../from-document-collection/reteco-v1.1-track1-monero.md) |
| Politics | [🔑](../from-document-collection/reteco-v1.1-track1-politics.md) |
| Quant | [🔑](../from-document-collection/reteco-v1.1-track1-quant.md) |
| Travel | [🔑](../from-document-collection/reteco-v1.1-track1-travel.md) |
| Workplace | [🔑](../from-document-collection/reteco-v1.1-track1-workplace.md) |

### Available Corpora for Download

| Corpora | Size | Checksum |
|:---|---:|:---|
| [Official RETECO v1.1 Track 1 corpora](https://huggingface.co/datasets/DataScience-UIBK/RETECO-SemEval2027/tree/c21e10b/track1_tempo) | — | — |

Download the v1.1 corpora at revision `c21e10b` and convert them to Anserini format:

```bash
set -e
base=https://huggingface.co/datasets/DataScience-UIBK/RETECO-SemEval2027/resolve/c21e10b/track1_tempo
for domain in bitcoin cardano economics genealogy history hsm iota law monero politics quant travel workplace; do
  input="collections/reteco-v1.1-raw/track1_tempo/$domain"
  mkdir -p "$input"
  wget -c "$base/$domain/documents.jsonl" -O "$input/documents.jsonl"
done
python3 src/main/python/reteco/convert_collection.py \
  --input collections/reteco-v1.1-raw --output collections/reteco
```

The RETECO corpora above are processed from Hugging Face with [this script](../../../src/main/python/reteco/convert_collection.py).

Alternatively, on Waterloo servers (e.g., `orca`), the corpora are available at `/store/collections/reteco/track1_tempo/<domain>/docs.jsonl`.
To use these existing corpora:

```bash
for domain in bitcoin cardano economics genealogy history hsm iota law monero politics quant travel workplace; do
  input="collections/reteco/track1_tempo/$domain"
  mkdir -p "$input"
  if [ ! -e "$input/docs.jsonl" ]; then
    ln -s "/store/collections/reteco/track1_tempo/$domain/docs.jsonl" "$input/docs.jsonl"
  fi
done
```
