package net.sf.extjwnl.data;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;

/**
 * A <code>Synset</code> for adjectives.
 *
 * @author Aliaksandr Autayeu <avtaev@gmail.com>
 */
public class AdjectiveSynset extends Synset {

    /**
     * for use only with WordNet 1.6 and earlier
     */
    private boolean isAdjectiveCluster = false;

    public AdjectiveSynset(Dictionary dictionary, POS pos) throws JWNLException {
        super(dictionary, pos);
    }

    public AdjectiveSynset(Dictionary dictionary, POS pos, long offset) throws JWNLException {
        super(dictionary, pos, offset);
    }

    public boolean isAdjectiveCluster() {
        return isAdjectiveCluster;
    }

    public void setIsAdjectiveCluster(boolean isAdjectiveCluster) {
        this.isAdjectiveCluster = isAdjectiveCluster;
    }
}