
package cn.com.nd.momo.api.util;

/**
 * 拼音匹配算法
 * 
 * @author chenjp
 */
public abstract class PinyinMatcher {
    protected boolean isFuzzyPinYinSupported = false;

    protected static final char fuzzyChar4 = '4';

    protected static final char fuzzyCharH = 'h';

    protected static final char[] EMPTY_CHAR_ARRAY = new char[0];

    protected static final String EMPTY_STRING = "";

    public abstract boolean doMatchForSingleChar(final String name, final char charToMatch);

    public abstract boolean doMatchForString(final String name, final char[] strToMatch);

    protected static final char[] fuzzySingleSylla = new char[] {
            'l', 'f', 'r', 'n', 'h', 'l'
    };

    protected static final char[] fuzzyMultiSyllaChar = new char[] {
            'z', 's', 'c'
    };

    protected static final char[] numOfFuzzyMultiSyllaChar = new char[] {
            '9', '7', '2'
    };

    protected static final char[] destFuzzySingleSylla = new char[] {
            'n', 'h', 'l', 'l', 'f', 'r'
    };

    protected static final char[] numOfFuzzySingleSylla = new char[] {
            '5', '3', '7', '6', '4', '5'
    };

    protected static final char[] numOfDestFuzzySingleSylla = new char[] {
            '6', '4', '5', '5', '3', '7'
    };

    protected static final String[] numOfDestFuzzySingleSyllaString = new String[] {
            "6", "4", "5", "5", "3", "7"
    };

    protected static final String[] fuzzyMultiSylla = new String[] {
            "zh", "sh", "ch"
    };

    protected static final String[] fuzzyMultiSyllaNumString = new String[] {
            "94", "74", "24"
    };

    public void setIsFuzzyPinYinSupported(boolean isFuzzyPinYinSupported) {
        this.isFuzzyPinYinSupported = isFuzzyPinYinSupported;
    }

    protected PinyinMatcher() {
    }

    protected boolean isIncludeSuchChar(char[] container, char suchChar) {
        for (int i = 0; i < container.length; i++) {
            if (container[i] == suchChar)
                return true;
        }
        return false;
    }

    protected boolean startsWithSuchCharArray(final String source, char[] target) {
        int targetLen = target.length;
        if (source.length() < targetLen)
            return false;
        for (int i = targetLen - 1; i >= 0; i--) {
            if (source.charAt(i) != target[i])
                return false;
        }
        return true;
    }
}
