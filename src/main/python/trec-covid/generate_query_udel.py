# -*- coding: utf-8 -*-
"""
Anserini: A toolkit for reproducible information retrieval research built on Lucene

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

import os
import argparse
import scispacy
import spacy
from lxml import etree


if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    # general settings
    parser.add_argument('--anserini_root', default='', help='Anserini path')
    parser.add_argument(
        '--round', default='1', choices=['1', '2', '3', '4', '5'],
        help='which round the query file should be generated for'
    )
    args = parser.parse_args()

    # stopwords from 
    # https://github.com/igorbrigadir/stopwords/edit/master/en/galago_inquery.txt
    stopwords = {
        'a', 'about', 'above', 'according', 'across', 'after', 'afterwards', 'again', 
        'against', 'albeit', 'all', 'almost', 'alone', 'along', 'already', 'also', 
        'although', 'always', 'am', 'among', 'amongst', 'an', 'and', 'another', 
        'any', 'anybody', 'anyhow', 'anyone', 'anything', 'anyway', 'anywhere', 
        'apart', 'are', 'around', 'as', 'at', 'av', 'be', 'became', 'because', 
        'become', 'becomes', 'becoming', 'been', 'before', 'beforehand', 'behind', 
        'being', 'below', 'beside', 'besides', 'between', 'beyond', 'both', 'but', 
        'by', 'can', 'cannot', 'canst', 'certain', 'cf', 'choose', 'contrariwise', 
        'cos', 'could', 'cu', 'day', 'do', 'does', 'doesn', 't', 'doing', 'dost', 
        'doth', 'double', 'down', 'dual', 'during', 'each', 'either', 'else', 
        'elsewhere', 'enough', 'et', 'etc', 'even', 'ever', 'every', 'everybody', 
        'everyone', 'everything', 'everywhere', 'except', 'excepted', 'excepting', 
        'exception', 'exclude', 'excluding', 'exclusive', 'far', 'farther', 
        'farthest', 'few', 'ff', 'first', 'for', 'formerly', 'forth', 'forward', 
        'from', 'front', 'further', 'furthermore', 'furthest', 'get', 'go', 'had', 
        'halves', 'hardly', 'has', 'hast', 'hath', 'have', 'he', 'hence', 
        'henceforth', 'her', 'here', 'hereabouts', 'hereafter', 'hereby', 'herein', 
        'hereto', 'hereupon', 'hers', 'herself', 'him', 'himself', 'hindmost', 'his', 
        'hither', 'hitherto', 'how','however', 'howsoever', 'i', 'ie', 'if', 'in', 
        'inasmuch', 'inc', 'include', 'included', 'including', 'indeed', 'indoors', 
        'inside', 'insomuch', 'instead', 'into', 'inward', 'inwards', 'is', 'it', 
        'its', 'itself', 'just', 'kg', 'kind', 'km', 'last', 'latter', 'latterly', 
        'less', 'lest', 'let', 'like', 'little', 'ltd', 'many', 'may', 'maybe', 'me', 
        'meantime', 'meanwhile', 'might', 'more','moreover', 'most', 'mostly', 'mr', 
        'mrs', 'ms', 'much', 'must', 'my', 'myself', 'namely', 'need', 'neither', 
        'never', 'nevertheless', 'next', 'no', 'nobody', 'none', 'nonetheless', 
        'noone', 'nope', 'nor', 'not', 'nothing', 'notwithstanding', 'now', 
        'nowadays', 'nowhere', 'of', 'off', 'often', 'ok', 'on', 'once', 'one', 
        'only', 'onto', 'or', 'other', 'others', 'otherwise', 'ought', 'our', 
        'ours', 'ourselves', 'out', 'outside', 'over', 'own', 'per', 'perhaps', 
        'plenty', 'provide', 'quite', 'rather', 'really', 'round', 'said', 'sake', 
        'same', 'sang', 'save', 'saw', 'see', 'seeing', 'seem', 'seemed', 'seeming', 
        'seems', 'seen', 'seldom', 'selves', 'sent', 'several', 'shalt', 'she', 
        'should', 'shown', 'sideways', 'since', 'slept', 'slew', 'slung', 'slunk', 
        'smote', 'so', 'some', 'somebody', 'somehow', 'someone', 'something', 
        'sometime', 'sometimes', 'somewhat', 'somewhere', 'spake', 'spat', 'spoke', 
        'spoken', 'sprang', 'sprung', 'stave', 'staves', 'still', 'such', 'supposing', 
        'than', 'that', 'the', 'thee', 'their', 'them', 'themselves', 'then', 'thence', 
        'thenceforth', 'there', 'thereabout', 'thereabouts', 'thereafter', 'thereby', 
        'therefore', 'therein', 'thereof', 'thereon', 'thereto', 'thereupon', 'these', 
        'they', 'this', 'those', 'thou', 'though', 'thrice', 'through', 'throughout', 
        'thru', 'thus', 'thy', 'thyself', 'till', 'to', 'together', 'too', 'toward', 
        'towards', 'ugh', 'unable', 'under', 'underneath', 'unless', 'unlike', 'until', 
        'up', 'upon', 'upward', 'upwards', 'us', 'use', 'used', 'using', 'very', 'via', 
        'vs', 'want', 'was', 'we', 'week', 'well', 'were', 'what', 'whatever', 
        'whatsoever', 'when', 'whence', 'whenever', 'whensoever', 'where', 'whereabouts', 
        'whereafter', 'whereas', 'whereat', 'whereby', 'wherefore', 'wherefrom', 
        'wherein', 'whereinto', 'whereof', 'whereon', 'wheresoever', 'whereto', 'whereunto', 
        'whereupon', 'wherever', 'wherewith', 'whether', 'whew', 'which', 'whichever', 
        'whichsoever', 'while', 'whilst', 'whither', 'who', 'whoa', 'whoever', 'whole', 
        'whom', 'whomever', 'whomsoever', 'whose', 'whosoever', 'why', 'will', 'wilt', 
        'with', 'within', 'without', 'worse', 'worst', 'would', 'wow', 'ye', 'year', 'yet', 
        'yippee', 'you', 'your', 'yours', 'yourself', 'yourselves'
    }

    nlp = spacy.load('en_core_sci_lg') 
    original_query_file_name = f'topics.covid-round{args.round}.xml'
    original_query_file = os.path.join(
        args.anserini_root,
        'src/main/resources/topics-and-qrels',
        original_query_file_name
    )

    output_query_file_name = f'topics.covid-round{args.round}-udel.xml'
    output_query_file = os.path.join(
        args.anserini_root,
        'src/main/resources/topics-and-qrels',
        output_query_file_name
    )

    root = etree.parse(original_query_file).getroot()
    for topic in root:
        qid = topic.attrib['number']
        for q in topic:
            if q.tag == 'query':
                query = q.text.strip()
                new = [w.text for w in nlp(query) if w.text not in stopwords]
            elif q.tag == 'question':
                question = q.text.strip()
                new += [w.text for w in nlp(question).ents]

        tags_to_be_removed = []
        for q in topic:
            if q.tag == 'query':
                q.text = ' '.join(new)
            else:
                tags_to_be_removed.append(q)

        for tag in tags_to_be_removed:
            topic.remove(tag)

    with open(output_query_file,'wb') as f:
        f.write(etree.tostring(root))

