/**
 * 
 */

package cn.com.nd.momo.api.util;

/**
 * @author chenjp
 */
public class QuanPinMatcher extends PinyinMatcher {

    private static QuanPinMatcher instance = null;

    public static QuanPinMatcher getInstance() {
        if (null == instance)
            instance = new QuanPinMatcher();
        return instance;
    }

    private boolean isMatch;

    char firstCharOfName;

    @Override
    public boolean doMatchForSingleChar(final String originalName, char charToMatch) {
        isMatch = false;
        String matchedStr = "";
        int len = originalName.length();
        if (len < 1)
            return isMatch;
        final String name = originalName.toLowerCase();
        firstCharOfName = name.charAt(0);
        if (firstCharOfName == charToMatch) {
            isMatch = true;
            matchedStr = String.valueOf(charToMatch);
        } else if (isFuzzyPinYinSupported && len > 1) {
            matchedStr = doMultiSyllaMatchForChar(name, charToMatch);
            if (!isMatch)
                matchedStr = doSingleSyllaMatchForChar(name, charToMatch);
        }
        ContactFilter.setMatchedStr(matchedStr);
        return isMatch;
    }

    private String doSingleSyllaMatchForChar(String name, char charToMatch) {
        if (firstCharOfName == charToMatch) {
            isMatch = true;
            return String.valueOf(charToMatch);
        } else {
            for (int i = 0; i < fuzzySingleSylla.length; i++) {
                char fuzzyKey = fuzzySingleSylla[i];
                if (firstCharOfName == fuzzyKey) {
                    char destchar = destFuzzySingleSylla[i];
                    if (charToMatch == destchar) {
                        isMatch = true;
                        return String.valueOf(fuzzyKey);
                    }
                }
            }
        }
        return "";
    }

    private String doMultiSyllaMatchForChar(String name, char charToMatch) {
        String headSecondChar = "";
        if (name.length() >= 2)
            headSecondChar = name.substring(0, 2);
        else
            headSecondChar = name.substring(0, 1);
        for (int i = 0; i < fuzzyMultiSylla.length; i++) {
            String fuzzyword = fuzzyMultiSylla[i];
            char firstFuzzyChar = fuzzyMultiSyllaChar[i];
            if (firstFuzzyChar == charToMatch && headSecondChar.equals(fuzzyword)) {
                isMatch = true;
                return headSecondChar;
            }
        }
        return "";
    }

    @Override
    public boolean doMatchForString(final String originalName, char[] strToMatch) {
        isMatch = false;
        String matchedStr = "";
        int len = originalName.length();
        if (len < 1)
            return isMatch;
        final String name = originalName.toLowerCase();
        if (startsWithSuchCharArray(name, strToMatch)) {
            isMatch = true;
            matchedStr = String.valueOf(strToMatch);
        } else if (isFuzzyPinYinSupported && len > 1) {
            firstCharOfName = name.charAt(0);
            matchedStr = doMultiSyllaMatchForString(name, strToMatch);
            if (!isMatch) {
                matchedStr = doSingleSyllaMatchForString(name, strToMatch);
            }
        }
        ContactFilter.setMatchedStr(matchedStr);
        return isMatch;
    }

    public String doSingleSyllaMatchForString(String name, char[] target) {
        char firstKeyChar = target[0];
        for (int i = 0; i < fuzzySingleSylla.length; i++) {
            char fuzzyKey = fuzzySingleSylla[i];
            if (firstKeyChar == fuzzyKey) {
                char destChar = destFuzzySingleSylla[i];
                char[] newSubKey = new char[target.length];
                System.arraycopy(target, 0, newSubKey, 0, target.length);
                newSubKey[0] = destChar;
                if (startsWithSuchCharArray(name, newSubKey)) {
                    isMatch = true;
                    return String.valueOf(newSubKey);
                }
                break;
            }
        }
        return "";
    }

    public String doMultiSyllaMatchForString(String name, char[] target) {
        int len = target.length;
        if (name.charAt(1) != fuzzyCharH) {
            return EMPTY_STRING;
        }
        for (int i = 0; i < fuzzyMultiSylla.length; i++) {
            char destChar = fuzzyMultiSyllaChar[i];
            if (firstCharOfName == destChar && target[0] == destChar) {
                char[] newSubKey = new char[len + 1];
                newSubKey[0] = destChar;
                newSubKey[1] = fuzzyCharH;
                System.arraycopy(target, 1, newSubKey, 2, len - 1);
                if (startsWithSuchCharArray(name, newSubKey)) {
                    isMatch = true;
                    return String.valueOf(newSubKey);
                }
                break;
            }
        }
        return "";
    }

}
