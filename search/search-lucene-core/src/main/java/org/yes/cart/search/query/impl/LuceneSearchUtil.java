/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.search.query.impl;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.yes.cart.search.utils.SearchUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 * 
 */
public class LuceneSearchUtil {

    private static Map<String, Analysis> LANGUAGE_SPECIFIC = new HashMap<>();
    static {
        initAnalysis();
        SearchUtil.addDestroyable(() -> destroy());
    }


    /**
     * Tokenize search phrase and clean from empty strings.
     *
     * @param lang   language to assume the phrase is in
     * @param phrase optional phrase
     *
     * @return list of tokens, that found in phrase.
     */
    public static List<String> analyseForSearch(final String lang, final String phrase) {
        final Analysis analysis = LANGUAGE_SPECIFIC.getOrDefault(lang, LANGUAGE_SPECIFIC.get("default"));
        return analysis.analyse(phrase);
    }

    private static class Analysis extends ThreadLocal<Analyzer> {

        List<String> analyse(String search) {

            final TokenStream stream = get().tokenStream("X", search);

            final List<String> result = new ArrayList<>();
            try {
                stream.reset();
                while(stream.incrementToken()) {
                    result.add(stream.getAttribute(TermToBytesRefAttribute.class).getBytesRef().utf8ToString());
                }
                stream.close();
            } catch (IOException e) {
                // OK
            }

            return result;

        }

    }

    private static void initAnalysis() {
        LANGUAGE_SPECIFIC.put("ru", new Analysis() {
            @Override
            protected Analyzer initialValue() {
                return new RussianAnalyzer();
            }
        });
        LANGUAGE_SPECIFIC.put("uk", new Analysis() {
            @Override
            protected Analyzer initialValue() {
                return new RussianAnalyzer();
            }
        });
        LANGUAGE_SPECIFIC.put("de", new Analysis() {
            @Override
            protected Analyzer initialValue() {
                return new GermanAnalyzer();
            }
        });
        LANGUAGE_SPECIFIC.put("fr", new Analysis() {
            @Override
            protected Analyzer initialValue() {
                return new FrenchAnalyzer();
            }
        });
        LANGUAGE_SPECIFIC.put("it", new Analysis() {
            @Override
            protected Analyzer initialValue() {
                return new ItalianAnalyzer();
            }
        });
        LANGUAGE_SPECIFIC.put("en", new Analysis() {
            @Override
            protected Analyzer initialValue() {
                return new StandardAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
            }
        });
        LANGUAGE_SPECIFIC.put("default", new Analysis() {
            @Override
            protected Analyzer initialValue() {
                return new StandardAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
            }
        });
    }

    /**
     * Explicitly remove thread locals to prevent memory leaks.
     */
    public static  void destroy() {
        for (final Map.Entry<String, Analysis> entry : LANGUAGE_SPECIFIC.entrySet()) {
            entry.getValue().remove(); // remove
        }
    }


}
