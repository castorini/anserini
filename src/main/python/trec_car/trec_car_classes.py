# Use python 3.5 or higher
# obsolete:  conda install -c auto cbor=0.1.4

from __future__ import print_function
import cbor
import itertools
import typing

PageId = str
PageName = str

class Page(object):
    """
    The name and skeleton of a Wikipedia page.

    .. attribute:: page_name

       :rtype: PageName

       The name of the page.

    .. attribute:: skeleton

       :rtype: typing.List[PageSkeleton]

       The contents of the page

    .. attribute:: page_type

       :rtype: PageType

       Type about the page

    .. attribute:: page_meta

       :rtype: PageMetadata

       Metadata about the page
    """
    def __init__(self, page_name, page_id, skeleton, page_type, page_meta):
        self.page_name = page_name
        self.page_id = page_id
        self.skeleton = list(skeleton)
        self.child_sections = [child for child in self.skeleton if isinstance(child, Section)]
        self.page_type = page_type
        self.page_meta = page_meta

    def deep_headings_list(self):
        return [child.nested_headings() for child in self.child_sections]

    def flat_headings_list(self):
        """
        Returns a flat list of headings contained by the :class:`Page`.

        :rtype: typing.List[Section]
        """
        def flatten(prefix, headings):
            for section, children in headings:
                new_prefix = prefix + [section]
                if len(children)>0 :
                    yield new_prefix
                    yield from flatten(new_prefix, children)
                else:
                    yield new_prefix

        deep_headings = self.deep_headings_list()
        return list(flatten([], deep_headings))

    @staticmethod
    def from_cbor(cbor):
        assert cbor[0] == 0 or cbor[0] == 1 # tag
        pagename = cbor[1]
        pageId = cbor[2].decode('ascii')

        if len(cbor) == 4:
            return Page(pagename, pageId, map(PageSkeleton.from_cbor, cbor[3]), ArticlePage, PageMetadata.default())
        else:
            page_type = PageType.from_cbor(cbor[4])
            return Page(pagename, pageId, map(PageSkeleton.from_cbor, cbor[3]), page_type, PageMetadata.from_cbor(cbor[5]))

    def __str__(self):
        return "Page(%s)" % self.page_name

    def to_string(self):
        """
        Render a string representation of the page.

        :rtype: str
        """
        return self.page_name + self.page_meta +\
               '\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~' + '\n'.join(str(s) for s in self.skeleton)

    def nested_headings(self):
        """
        Each heading recursively represented by a pair of ``(heading,
        list_of_child_sections)``.

        :rtype: typing.List[typing.Tuple[Section, typing.List[Section]]]
        """
        result = [child.nested_headings() for child in self.child_sections]
        return result

    def outline(self):
        return self.child_sections

    def get_text(self):
        return '\n'.join(skel.get_text() for skel in self.skeleton)

class PageType(object):
    """
    An abstract base class representing the various types of pages.

    Subclasses include

    * :class:`ArticlePage`
    * :class:`CategoryPage`
    * :class:`DisambiguationPage`
    * :class:`RedirectPage`
    """
    @staticmethod
    def from_cbor(cbor):
        typetag = cbor[0]
        if typetag == 0: return ArticlePage()
        elif typetag == 1: return CategoryPage()
        elif typetag == 2: return DisambiguationPage()
        elif typetag == 3:
            targetPage = cbor[1].decode('ascii')
            return RedirectPage(targetPage)
        else:
            print("Deserialisation error for PageType cbor="+cbor)
            assert(False)

class ArticlePage(PageType):
    ''
    def __init__(self):
        pass
    def __str__(self): return "ArticlePage"

class CategoryPage(PageType):
    def __init__(self):
        pass
    def __str__(self): return "CategoryPage"

class DisambiguationPage(PageType):
    def __init__(self):
        pass
    def __str__(self): return "Disambiguation Page"

class RedirectPage(PageType):
    """
    .. attribute:: targetPage

       :rtype: PageId

       The target of the redirect.
    """
    def __init__(self, targetPage):
        self.targetPage = targetPage
    def __str__(self):
        return "RedirectPage " + self.targetPage

class PageMetadata(object):
    """
    Meta data for a page

    .. attribute:: redirectNames

        :rtype: PageName

        Names of pages which redirect to this page

    .. attribute:: disambiguationNames

        :rtype: PageName

        Names of disambiguation pages which link to this page

    .. attribute:: disambiguationId

        :rtype: PageId

        Page IDs of disambiguation pages which link to this page

    .. attribute:: categoryNames

        :rtype: str

        Page names of categories to which this page belongs

    .. attribute:: categoryIds

        :rtype: str

        Page IDs of categories to which this page belongs

    .. attribute:: inlinkIds

        :rtype: str

        Page IDs of pages containing inlinks

    .. attribute:: inlinkAnchors
       inlinkAnchor frequencies

        :rtype: str

        (Anchor text, frequency) of pages containing inlinks
    """
    def __init__(self, redirectNames, disambiguationNames, disambiguationIds, categoryNames, categoryIds, inlinkIds,
                 inlinkAnchors):
        self.inlinkAnchors = inlinkAnchors
        self.inlinkIds = inlinkIds
        self.categoryIds = categoryIds
        self.categoryNames = categoryNames
        self.disambiguationIds = disambiguationIds
        self.disambiguationNames = disambiguationNames
        self.redirectNames = redirectNames

    @staticmethod
    def default():
        return PageMetadata(None, None, None, None, None, None, None)

    def __str__(self):
        redirStr = ("" if self.redirectNames is None else (" redirected = "+", ".join([name for name in self.redirectNames])))
        disamStr = ("" if self.disambiguationNames is None else (" disambiguated = "+", ".join([name for name in self.disambiguationNames])))
        catStr = ("" if self.redirectNames is None else (" categories = "+", ".join([name for name in self.categoryNames])))
        inlinkStr = ("" if self.inlinkIds is None else (" inlinks = "+", ".join([name for name in self.inlinkIds])))
        inlinkAnchorStr = ("" if self.inlinkAnchors is None else \
                                (" inlinkAnchors = "+", ".join( \
                                    [("%s: %d" % (name, freq)) for (name, freq) in self.inlinkAnchors])))
        return  "%s \n%s \n%s \n%s \n%s\n" % (redirStr, disamStr, catStr, inlinkStr, inlinkAnchorStr)

    @staticmethod
    def from_cbor(cbor):
        redirectNames = None
        disambiguationNames = None
        disambiguationIds = None
        categoryNames = None
        categoryIds = None
        inlinkIds = None
        inlinkAnchors = None

        
        def decode_list_of_id_list(cbor):
            if len(cbor) == 0:
                return None
            else:
                return [elem.decode('ascii') for elem in cbor]

        def decode_list_of_name_list(cbor):
            if len(cbor) == 0:
                return None
            else:
                return cbor

        def decode_list_of_name_int_list(cbor):
            if len(cbor) == 0: return None
            else:
                # Need to convert list of pair-lists to lists of pair-tuples.
                return [(elem[0], elem[1]) for elem in cbor]

        for i in range(0, len(cbor), 2):
            tag = cbor[i][0]
            cbor_data = cbor[i + 1]

            if tag == 0:
                redirectNames = decode_list_of_name_list(cbor_data)
            elif tag == 1:
                disambiguationNames = decode_list_of_name_list(cbor_data)
            elif tag == 2:
                disambiguationIds = decode_list_of_id_list(cbor_data)
            elif tag == 3:
                categoryNames = decode_list_of_name_list(cbor_data)
            elif tag == 4:
                categoryIds = decode_list_of_id_list(cbor_data)
            elif tag == 5:
                inlinkIds = decode_list_of_id_list(cbor_data)

            elif tag == 6:
                # Compatability with v1.6.
                inlinkAnchors = [(anchor, 1) for anchor in decode_list_of_name_list(cbor_data)]
            elif tag == 7:
                # Compatability with v2.0.
                inlinkAnchors = decode_list_of_name_int_list(cbor_data)
            i+=2

        return PageMetadata(redirectNames, disambiguationNames, disambiguationIds, categoryNames, categoryIds, inlinkIds, inlinkAnchors)

class PageSkeleton(object):
    """
    An abstract superclass for the various types of page elements. Subclasses include:

    * :class:`Section`
    * :class:`Para`
    * :class:`Image`

    """
    @staticmethod
    def from_cbor(cbor):
        tag = cbor[0]
        if tag == 0:
            heading = cbor[1]
            headingId = cbor[2].decode('ascii')
            return Section(heading, headingId, map(PageSkeleton.from_cbor, cbor[3]))
        elif tag == 1:
            return Para(Paragraph.from_cbor(cbor[1]))
        elif tag == 2:
            imageUrl = cbor[1]
            caption = [PageSkeleton.from_cbor(elem) for elem in cbor[2]]
            return Image(imageUrl, caption=caption)
        elif tag == 3:
            level = cbor[1]
            body = Paragraph.from_cbor(cbor[2])
            return List(level, body)
        else:
            assert(False)

    def get_text(self):
        raise NotImplementedError

class Section(PageSkeleton):
    """
    A section of a Wikipedia page.

    .. attribute:: heading

       :rtype: str

       The section heading.

    .. attribute:: children

       :rtype: typing.List[PageSkeleton]

       The :class:`PageSkeleton` elements contained by the section.
    """
    def __init__(self, heading, headingId, children):
        self.heading = heading
        self.headingId = headingId
        self.children = list(children)
        self.child_sections =  [child for child in self.children if isinstance(child, Section)]

    def __str__(self, level=1):
        bar = "".join("="*level)
        children = "".join(c.__str__(level=level+1) for c in self.children)
        return "\n%s %s %s\n\n%s" % (bar, self.heading, bar, children)

    def __getitem__(self, idx):
        return self.children[idx]

    def nested_headings(self):
        return (self, [child.nested_headings() for child in self.child_sections])

    def get_text(self):
        return '\n'.join(child.get_text() for child in self.children)

class Para(PageSkeleton):
    """
    A paragraph within a Wikipedia page.

    .. attribute:: paragraph

       :rtype: Paragraph

       The content of the Paragraph (which in turn contain a list of :class:`ParaBody`\ s)
    """
    def __init__(self, paragraph):
        self.paragraph = paragraph

    def __str__(self, level=None):
        return str(self.paragraph)

    def get_text(self):
        return self.paragraph.get_text()

class Image(PageSkeleton):
    """
    An image within a Wikipedia page.

    .. attribute:: caption

       :rtype: str

       PageSkeleton representing the caption of the image

    .. attribute:: imageurl

       :rtype: str

       URL to the image; spaces need to be replaced with underscores, Wikimedia
       Commons namespace needs to be prefixed
    """
    def __init__(self, imageurl, caption):
        self.caption = caption
        self.imageurl = imageurl

    def __str__(self, level=None):
        return str("!["+self.imageurl+"]. Caption: "+(''.join([str(skel) for skel in self.caption])))

    def get_text(self):
        return '\n'.join(skel.get_text() for skel in self.caption)

class List(PageSkeleton):
    """
    An list element within a Wikipedia page.

    .. attribute:: level

       :rtype: int

       The list nesting level

    .. attribute::  body

       A :class:`Paragraph` containing the list element contents.
    """
    def __init__(self, level, body):
        self.level = level
        self.body = body

    def __str__(self, level=None):
        return str("*" * self.level + " " + str(self.body) + '\n')

    def get_text(self):
        return self.body.get_text()

class Paragraph(object):
    """
    A paragraph.
    """
    def __init__(self, para_id, bodies):
        self.para_id = para_id
        self.bodies = list(bodies)

    @staticmethod
    def from_cbor(cbor):
        assert cbor[0] == 0
        paragraphId = cbor[1].decode('ascii')
        return Paragraph(paragraphId, map(ParaBody.from_cbor, cbor[2]))

    def get_text(self):
        """
        Get all of the contained text.

        :rtype: str
        """
        return ''.join([body.get_text() for body in self.bodies])

    def __str__(self, level=None):
        return ' '.join(str(body) for body in self.bodies)

class ParaBody(object):
    """
    An abstract superclass representing a bit of :class:`Paragraph` content.
    """
    @staticmethod
    def from_cbor(cbor):
        tag = cbor[0]
        if tag == 0:
            return ParaText(cbor[1])
        elif tag == 1:
            cbor_ = cbor[1]
            linkSection = None
            if len(cbor_[2]) == 1:
                linkSection = cbor_[2][0]
            linkTargetId = cbor_[3].decode('ascii')
            return ParaLink(cbor_[1], linkSection, linkTargetId, cbor_[4])
        else:
            assert(False)

    def get_text(self):
        raise NotImplementedError

    def get_text(self):
        """
        Get all of the text within a :class:`ParaBody`.

        :rtype: str
        """
        raise NotImplementedError

class ParaText(ParaBody):
    """
    A bit of plain text from a paragraph.

    .. attribute:: text

       :rtype: str

       The text
    """
    def __init__(self, text):
        self.text = text

    def get_text(self):
        return self.text

    def __str__(self, level=None):
        return self.text

class ParaLink(ParaBody):
    """
    A link within a paragraph.

    .. attribute:: page

       :rtype: PageName

       The page name of the link target

    .. attribute:: pageid

       :rtype: PageId

       The link target as trec-car identifer

    .. attribute:: link_section

       :rtype: str

       Section anchor of link target (i.e. the part after the ``#`` in the
       URL), or ``None``.

    .. attribute:: anchor_text

       :rtype: str

       The anchor text of the link
    """
    def __init__(self, page, link_section, pageid, anchor_text):
        self.page = page
        self.pageid = pageid
        self.link_section = link_section
        self.anchor_text = anchor_text

    def get_text(self):
        return self.anchor_text

    def __str__(self, level=None):
        return "[%s](%s)" % (self.anchor_text, self.page)

def _iter_with_header(file, parse, expected_file_types):
    maybe_hdr = cbor.load(file)
    if isinstance(maybe_hdr, list) and maybe_hdr[0] == 'CAR':
        # We have a header.
        file_type = maybe_hdr[1][0]
        assert file_type in expected_file_types

        # Read beginning of variable-length list.
        assert file.read(1) == b'\x9f'
    else:
        yield parse(maybe_hdr)

    while True:
        try:
            # Check for break symbol.
            if (peek_for_break(file)):
                break

            yield parse(cbor.load(file))
        except EOFError:
            break

def peek_for_break(cbor):
    b = cbor.peek(1)
    return b[0:1] == b'\xff'


def iter_annotations(file):
    """
    Iterate over the :class:`Page`\ s of an annotations file.

    :type file: typing.TextIO
    :rtype: typing.Iterator[Page]
    """
    return _iter_with_header(file, Page.from_cbor, [0,1])



def iter_pages(file):
    """
    Iterate over the :class:`Page`\ s of an annotations file.

    :type file: typing.TextIO
    :rtype: typing.Iterator[Page]
    """
    return _iter_with_header(file, Page.from_cbor, [0])



def iter_outlines(file):
    """
    Iterate over the :class:`Page`\ s of an annotations file.

    :type file: typing.TextIO
    :rtype: typing.Iterator[Page]
    """
    return _iter_with_header(file, Page.from_cbor, [1])


def iter_paragraphs(file):
    """
    Iterate over the :class:`Paragraph`\ s of an paragraphs file.

    :type file: typing.TextIO
    :rtype: typing.Iterator[Paragraph]
    """
    return _iter_with_header(file, Paragraph.from_cbor, [2])

def dump_annotations(file):
    for page in iter_annotations(file):
        print(page.to_string())

def with_toc(read_val):
    class AnnotationsFile(object):
        def __init__(self, fname):
            """
            Read annotations from a file.

            Arguments:
            fname      The name of the CBOR file. A table-of-contents file is
                        also expected to be present.
            """
            self.cbor = open(fname, 'rb')
            self.toc  = cbor.load(open(fname+'.toc', 'rb'))

        def keys(self):
            """ The page names contained in an annotations file. """
            return self.toc.keys()

        def get(self, page):
            """ Lookup a page by name. Returns a Page or None """
            offset = self.toc.get(page)
            if offset is not None:
                self.cbor.seek(offset)
                return read_val(cbor.load(self.cbor))
            return None
    return AnnotationsFile

AnnotationsFile = with_toc(Page.from_cbor)
ParagraphsFile = with_toc(Paragraph.from_cbor)
