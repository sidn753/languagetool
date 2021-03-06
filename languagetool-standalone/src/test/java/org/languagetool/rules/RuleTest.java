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
package org.languagetool.rules;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.rules.patterns.PatternRule;

public class RuleTest extends TestCase {

  public void testJavaRules() throws IOException {
    final Set<String> ids = new HashSet<>();
    final Set<Class> ruleClasses = new HashSet<>();
    if (Language.LANGUAGES.length <= 1) {
      System.err.println("***************************************************************************");
      System.err.println("WARNING: found only these languages - the tests might not be complete:");
      System.err.println(Arrays.toString(Language.LANGUAGES));
      System.err.println("***************************************************************************");
    }
    for (Language language : Language.LANGUAGES) {
      final JLanguageTool lt = new JLanguageTool(language);
      final List<Rule> allRules = lt.getAllRules();
      for (Rule rule : allRules) {
        if (rule instanceof PatternRule) {
          throw new RuntimeException("Did not expect PatternRule here: " + rule);
        } else {
          assertIdUniqueness(ids, ruleClasses, language, rule);
          assertIdValidity(language, rule);
          assertTrue(rule.supportsLanguage(language));
          testExamples(rule, lt);
        }
      }
    }
  }

  private void assertIdUniqueness(Set<String> ids, Set<Class> ruleClasses, Language language, Rule rule) {
    final String ruleId = rule.getId();
    if (ids.contains(ruleId) && !ruleClasses.contains(rule.getClass())) {
      throw new RuntimeException("Rule id occurs more than once: '" + ruleId + "', language: " + language);
    }
    ids.add(ruleId);
    ruleClasses.add(rule.getClass());
  }

  private void assertIdValidity(Language language, Rule rule) {
    final String ruleId = rule.getId();
    if (!ruleId.matches("^[A-Z_]+$")) {
      throw new RuntimeException("Invalid character in rule id: '" + ruleId + "', language: "
              + language + ", only [A-Z_] are allowed");
    }
  }

  private void testExamples(Rule rule, JLanguageTool lt) throws IOException {
    testCorrectExamples(rule, lt);
    testIncorrectExamples(rule, lt);
  }

  private void testCorrectExamples(Rule rule, JLanguageTool lt) throws IOException {
    List<String> correctExamples = rule.getCorrectExamples();
    for (String correctExample : correctExamples) {
      String input = cleanMarkers(correctExample);
      enableOnlyOneRule(lt, rule);
      List<RuleMatch> ruleMatches = lt.check(input);
      assertEquals("Got unexpected rule match for correct example sentence:\n"
              + "Text: " + input + "\n"
              + "Rule: " + rule.getId() + "\n"
              + "Matches: " + ruleMatches, 0, ruleMatches.size());
    }
  }

  private void testIncorrectExamples(Rule rule, JLanguageTool lt) throws IOException {
    List<IncorrectExample> incorrectExamples = rule.getIncorrectExamples();
    for (IncorrectExample incorrectExample : incorrectExamples) {
      String input = cleanMarkers(incorrectExample.getExample());
      enableOnlyOneRule(lt, rule);
      List<RuleMatch> ruleMatches = lt.check(input);
      assertEquals("Did not get the expected rule match for the incorrect example sentence:\n"
              + "Text: " + input + "\n"
              + "Rule: " + rule.getId() + "\n"
              + "Matches: " + ruleMatches, 1, ruleMatches.size());
    }
  }

  private void enableOnlyOneRule(JLanguageTool lt, Rule ruleToActivate) {
    for (Rule rule : lt.getAllRules()) {
      lt.disableRule(rule.getId());
    }
    lt.enableRule(ruleToActivate.getId());
    //make sure that the rule that is by default off is on
    lt.enableDefaultOffRule(ruleToActivate.getId());
  }

  private String cleanMarkers(String example) {
    return example.replace("<marker>", "").replace("</marker>", "");
  }

}
