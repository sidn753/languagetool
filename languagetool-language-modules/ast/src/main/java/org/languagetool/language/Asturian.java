/* LanguageTool, a natural language style checker 
 * Copyright (C) 2011 Daniel Naber (http://www.danielnaber.de)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.language;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.languagetool.Language;
import org.languagetool.language.rules.ast.MorfologikAsturianSpellerRule;
import org.languagetool.rules.*;
import org.languagetool.tagging.Tagger;
import org.languagetool.tagging.ast.AsturianTagger;
import org.languagetool.tokenizers.SRXSentenceTokenizer;
import org.languagetool.tokenizers.SentenceTokenizer;

public class Asturian extends Language {

  private Tagger tagger;
  private SentenceTokenizer sentenceTokenizer;
  private String name = "Asturian";

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getShortName() {
    return "ast";
  }

  @Override
  public String[] getCountries() {
    return new String[]{"ES"};
  }

  @Override
  public Contributor[] getMaintainers() {
    return new Contributor[] { new Contributor("Xesús González Rato") };
  }

  @Override
  public List<Rule> getRelevantRules(ResourceBundle messages) throws IOException {
    return Arrays.asList(
            new CommaWhitespaceRule(messages),
            new DoublePunctuationRule(messages),
            new GenericUnpairedBracketsRule(messages, this),
            new MorfologikAsturianSpellerRule(messages, this),
            new UppercaseSentenceStartRule(messages, this),
            new MultipleWhitespaceRule(messages, this)
    );
  }

  @Override
  public SentenceTokenizer getSentenceTokenizer() {
    if (sentenceTokenizer == null) {
      sentenceTokenizer = new SRXSentenceTokenizer(this);
    }
    return sentenceTokenizer;
  }

  @Override
  public Tagger getTagger() {
    if (tagger == null) {
      tagger = new AsturianTagger();
    }
    return tagger;
  }

}
