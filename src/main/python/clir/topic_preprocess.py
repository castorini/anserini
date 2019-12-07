import argparse
import xml.etree.ElementTree as ET
import bs4


def zh_topic(input_file, output_file, target_language):
    with open(output_file, 'w') as fout:
        root = ET.parse(input_file)
        topics = root.findall("TOPIC")
        for topic in topics:
            topic_id = topic.attrib['ID']
            questions = topic.findall("QUESTION")
            text = None
            for question in questions:
                if question.attrib["LANG"] == target_language:
                    text = question.text
            if text is None:
                print("Could not find target language {}".format(target_language))
            fout.write("{}\t{}\n".format(topic_id, text))


def ar_topic(input_file, output_file):
    fout = open(output_file, 'w')
    with open(input_file) as fin:
        for line in fin:
            if line.startswith("<num>"):
                qid = line.strip().split()[-1][2:]
            if line.startswith("<title>"):
                topic = line.replace("<title>", "").strip()
                fout.write("{}\t{}\n".format(qid, topic))


def fr_topic(input_file, output_file):
    fout = open(output_file, 'w')
    root = ET.parse(input_file)
    topics = root.findall("topic")
    for topic in topics:
        topic_id, topic_text = topic.find("identifier").text, topic.find("title").text
        fout.write("{}\t{}\n".format(topic_id, topic_text))


def fire_topic(input_file, output_file):
    fout = open(output_file, 'w')
    content = "".join(open(input_file).readlines())
    root = bs4.BeautifulSoup(content)

    topics = root.find_all("top")
    for topic in topics:
        topic_id, topic_text = topic.find("num").text, topic.find("title").text
        fout.write("{}\t{}\n".format(topic_id, topic_text))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--topic_file_sinput")
    parser.add_argument("--topic_file_output")
    parser.add_argument("--language", choices=["zh", "ar", "fr", "hi", "bn", "en"])
    parser.add_argument("--target_language", choices=["EN", "CS"])
    args = parser.parse_args()

    if args.language == "zh":
        zh_topic(args.topic_file_input, args.topic_file_output, args.target_language)
    elif args.language == "ar":
        ar_topic(args.topic_file_input, args.topic_file_output)
    elif args.language == "fr":
        fr_topic(args.topic_file_input, args.topic_file_output)
    elif args.language == "hi" or args.language == "bn" or args.language == "en":
        fire_topic(args.topic_file_input, args.topic_file_output)
