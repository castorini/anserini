/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.analysis;

import com.twitter.twittertext.Regex;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

import java.io.IOException;
import java.util.Arrays;

public final class TweetLowerCaseEntityPreservingFilter extends TokenFilter {

  private static final int INVALID_ENTITY = 0;
  private static final int VALID_HASHTAG = 1;
  private static final int VALID_MENTION = 2;
  private static final int VALID_URL = 3;

  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);
  private char[] tailBuffer = null;
  private char[] tailBufferSaved = null;

  public TweetLowerCaseEntityPreservingFilter(TokenStream in) {
    super(in);
  }

  @Override
  public boolean incrementToken() throws IOException {
    // There is no saved state, and nothing remains on the input buffer
    if (tailBuffer == null && tailBufferSaved == null && !input.incrementToken()) {
      return false;
    }

    final char[] buffer = termAtt.buffer();

    // Reload any saved sate
    if (tailBuffer != null) {
      System.arraycopy(tailBuffer, 0, buffer, 0, tailBuffer.length);
      termAtt.setLength(tailBuffer.length);
      tailBuffer = null;
    } else  if (tailBufferSaved != null) {
      System.arraycopy(tailBufferSaved, 0, buffer, 0, tailBufferSaved.length);
      termAtt.setLength(tailBufferSaved.length);
      tailBufferSaved = null;
    }

    int entityType = isEntity(termAtt.toString());
    if (entityType == VALID_URL) {
      keywordAttr.setKeyword(true);
      return true;
    }

    if (entityType != INVALID_ENTITY) {
      // Lowercase the token
      for (int i = 0; i < termAtt.length(); i++) {
        buffer[i] = Character.toLowerCase(buffer[i]);
      }
      
      // At this stage, if it's a valid entity and doesn't have any
      // proceeding characters, then we can stop processing
      if (isEntityDelimiter(0)) {
        keywordAttr.setKeyword(true);
        return true;
      }

      // Cases where there are characters before the entity sign.
      // Split them off and process them separately
      for (int i = 0; i < termAtt.length(); i++) {
        if (isEntityDelimiter(i)) {
          tailBuffer = Arrays.copyOfRange(buffer, i, termAtt.length());
          termAtt.setLength(i);
          break;
        }
      }

    } else {

      // Check for non-whitespace, non-entity (@, #, _) delimiters in the
      // term
      for (int i = 0; i < termAtt.length(); i++) {
        if (isNonentityDelimiter(i)) {
          // Remove the tail of the string from the buffer and save it
          // for the next iteration
          tailBuffer = Arrays.copyOfRange(buffer, i + 1, termAtt.length());
          termAtt.setLength(i);
          break;
        }
      }
      
      // TODO: Preserve Email Addresses

      if (isEntity(termAtt.toString()) != INVALID_ENTITY) {
        // This was an entity with some trailing text - we've removed
        // the tail, all that remains is the entity
        keywordAttr.setKeyword(true);
        
        for (int i = 0; i < termAtt.length(); i++) {
          buffer[i] = Character.toLowerCase(buffer[i]);
        }
        return true;
      }

      // It wasn't an entity - we can use the entity markers as delimiters
      for (int i = 0; i < termAtt.length(); i++) {
        if (isEntityDelimiter(i)) {
          // Remove the tail of the string from the buffer and save it
          // for the next iteration
          if(tailBuffer != null) {
            tailBufferSaved = tailBuffer;
          }
          tailBuffer = Arrays.copyOfRange(buffer, i + 1, termAtt.length());
          termAtt.setLength(i);
          break;
        }
      }

      for (int i = 0; i < termAtt.length(); i++) {
        buffer[i] = Character.toLowerCase(buffer[i]);
      }
    }

    removeNonAlphanumeric();
    return true;
  }

  /**
   * Removes all non-alphanumeric characters from the buffer.
   */
  public void removeNonAlphanumeric() {
    final char[] buffer = termAtt.buffer();
    // Remove any remaining non-alphanumeric characters
    for (int i = 0; i < termAtt.length(); i++) {
      // TODO: isAlphabetic is a better choice than isLetter since it scrubs some weird characters,
      // but isAlphabetic is a JDK7 method.
      if (!(Character.isLetter(buffer[i]) || Character.isDigit(buffer[i]))) {
        System.arraycopy(buffer, i + 1, buffer, i, buffer.length - 1 - i);
        termAtt.setLength(termAtt.length() - 1);
        i--; // Correct for the (now displaced) buffer position
      }
    }
  }

  /**
   * Checks if the given string is a valid entity (mention, hashtag or URL).
   *
   * @param term string to check
   * @return one of {@code INVALID_ENTITY}, {@code VALID_HASHTAG},
   *         {@code VALID_MENTION}, {@code VALID_URL}
   */
  public int isEntity(String term) {
    if (Regex.VALID_URL.matcher(term).matches())
      return VALID_URL;
    else if (Regex.VALID_MENTION_OR_LIST.matcher(term).matches())
      return VALID_MENTION;
    else if (Regex.VALID_HASHTAG.matcher(term).matches())
      return VALID_HASHTAG;
    else
      return INVALID_ENTITY;
  }

  /**
   * Checks if the character at position <i>i</i> in the buffer is a delimiter that
   * shouldn't be used as part of an entity.
   *
   * @param i index position
   * @return if character a non-entity delimiter
   */
  public boolean isNonentityDelimiter(int i) {
    final char[] buffer = termAtt.buffer();
    final int bufferLength = termAtt.length();
    switch (buffer[i]) {
    case '[':
    case ']':
    case '!':
    case '"':
    case '$':
    case '%':
    case '(':
    case ')':
    case '*':
    case '+':
    case ',':
    case '/':
    case ':':
    case ';':
    case '<':
    case '=':
    case '>':
    case '?':
    case '\\':
    case '^':
    case '`':
    case '{':
    case '|':
    case '}':
    case '~':
    // Note that following are non-ASCII code characters. To compile correctly, need to make sure:
    //   javac -encoding utf8 SourceFile.java
    //
    // otherwise, javac uses platform default encoding,
    // which may lead to unpredictable results. In ant:
    //   <javac ... encoding="utf-8">
    case '-':
    case '…':
    case '¬':
    case '·':
      return true;
    case '.':
      // A complex looking way of saying that a period isn't a delimiter if the
      // characters at current_position +/- 2 are also periods.
      return (i >= 2 && buffer[i - 2] != '.') || ((i + 2) < bufferLength && buffer[i + 2] != '.');
    case '&':
      // Preserve cases such as "AT&T" (i.e. uppercase characters on both sized of the ampersand)
      return i < 1 || (i + 1) >= bufferLength || Character.isLowerCase(buffer[i - 1]) || Character.isLowerCase(buffer[i + 1]);
    }
    return false;
  }

  /**
   * Checks if the character at position <i>i</i> in the buffer is a delimiter that
   * can be used as part of an entity.
   *
   * @param i index position
   * @return if character an entity delimiter
   */
  public boolean isEntityDelimiter(int i) {
    final char[] buffer = termAtt.buffer();
    switch (buffer[i]) {
    case '@':
    case '\uFF20': // Unicode @
    case '#':
    case '\uFF03': // Unicode #
    case '_':
      return true;
    }
    return false;
  }

  /**
   * Checks if the character at position <i>i</i> in the buffer is a delimiter.
   *
   * @param i index position
   * @return if character a delimiter
   */
  public boolean isDelimiter(int i) {
    return isNonentityDelimiter(i) || isEntityDelimiter(i);
  }
}