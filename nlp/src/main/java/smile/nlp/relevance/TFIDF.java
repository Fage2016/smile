/*
 * Copyright (c) 2010-2025 Haifeng Li. All rights reserved.
 *
 * Smile is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Smile is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Smile. If not, see <https://www.gnu.org/licenses/>.
 */
package smile.nlp.relevance;

import smile.nlp.Corpus;
import smile.nlp.TextTerms;

/**
 * The tf-idf weight (term frequency-inverse document frequency) is a weight
 * often used in information retrieval and text mining. This weight is a
 * statistical measure used to evaluate how important a word is to a document
 * in a collection or corpus. The importance increases proportionally to the
 * number of times a word appears in the document but is offset by the
 * frequency of the word in the corpus. Variations of the tf-idf weighting
 * scheme are often used by search engines as a central tool in scoring and
 * ranking a document's relevance given a user query. One of the simplest
 * ranking functions is computed by summing the tf-idf for each query term;
 * many more sophisticated ranking functions are variants of this simple model.
 * <p>
 * One well-studied technique is to normalize the tf weights of all terms
 * occurring in a document by the maximum tf in that document. For each document
 * d, let tfmax(d) be the maximum tf over all terms in d. Then, we compute a
 * normalized term frequency for each term t in document d by
 * <p>
 * tf = a + (1? a) tf<sub>t,d</sub> / tfmax(d)
 * <p>
 * where a is a value between 0 and 1 and is generally set to 0.4, although some
 * early work used the value 0.5. The term a is a smoothing term whose role is
 * to damp the contribution of the second term - which may be viewed as a scaling
 * down of tf by the largest tf value in d. The main idea of maximum tf
 * normalization is to mitigate the following anomaly: we observe higher term
 * frequencies in longer documents, merely because longer documents tend to
 * repeat the same words over and over again. Maximum tf normalization does
 * suffer from the following issues:
 * <ol>
 * <li> The method is unstable in the following sense: a change in the stop word
 * list can dramatically alter term weightings (and therefore ranking). Thus,
 * it is hard to tune.
 * <li> A document may contain an outlier term with an unusually large number
 * of occurrences of that term, not representative of the content of that document.
 * <li> More generally, a document in which the most frequent term appears
 * roughly as often as many other terms should be treated differently from
 * one with a more skewed distribution.
 * </ol>
 *
 * @see BM25
 *
 * @author Haifeng Li
 */
public class TFIDF implements RelevanceRanker {
    /**
     * The smoothing parameter in maximum tf normalization.
     */
    private final double a;

    /**
     * Constructor.
     */
    public TFIDF() {
        this(0.4);
    }

    /**
     * Constructor.
     * @param smoothing the smoothing parameter in maximum tf normalization.
     */
    public TFIDF(double smoothing) {
        a = smoothing;
    }

    /**
     * Returns the relevance score between a term and a document based on a corpus.
     * @param tf the frequency of searching term in the document to rank.
     * @param maxtf the maximum frequency over all terms in the document.
     * @param N the number of documents in the corpus.
     * @param n the number of documents containing the given term in the corpus;
     * @return the relevance score.
     */
    public double rank(int tf, int maxtf, long N, long n) {
        if (tf == 0) return 0.0;

        return (a + (1-a) * tf / maxtf) * Math.log((double) N / n);
    }

    @Override
    public double rank(Corpus corpus, TextTerms doc, String term, int tf, int n) {
        if (tf == 0) return 0.0;

        int N = corpus.docCount();
        int maxtf = doc.maxtf();

        return rank(tf, maxtf, N, n);
    }

    @Override
    public double rank(Corpus corpus, TextTerms doc, String[] terms, int[] tf, int n) {
        int N = corpus.docCount();
        int maxtf = doc.maxtf();

        double r = 0.0;
        for (int i = 0; i < terms.length; i++) {
            r += rank(tf[i], maxtf, N, n);
        }

        return r;
    }
}
