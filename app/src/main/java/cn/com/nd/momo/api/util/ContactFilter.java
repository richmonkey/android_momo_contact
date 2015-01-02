
package cn.com.nd.momo.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Handler;
import android.os.Message;
import cn.com.nd.momo.api.types.Contact;

/**
 * 通讯簿搜索过虑器
 * 
 * @author chenjp
 */
public final class ContactFilter {
    private static Contact[] toBeFilterContactsArray;

    private static final int MAX_FILTER_LENGTH = 20;

    public static String wordBreaker = " ";

    public static char wordBreakerChar = ' ';

    public static String multiSyllaBreaker = ",";

    public static char multiSyllaBreakerChar = ',';

    // 是否支持模糊音
    private static boolean isFuzzyPinYinSupported = true;

    // 普通匹配
    private final static int NORMAL_MATCH_WEIGHT = 3;

    // 每字一个拼音首字母
    private final static int EACH_FIRST_LETER_MATCH_WEIGHT = 12;

    // 中间没有断
    private final static int NOTBREAK_MATCH_WEIGHT = 5;

    // 名字的长度，名字越短，排得越靠前
    private final static int NAME_LENGTH_WEIGHT = 1;

    // 匹配到姓额外加分
    private final static int LASTNAME_MATCH_WEIGHT = 6;

    // 整字匹配
    private final static int WHOLE_WORD_MATCH_WEIGHT = 3;

    private static boolean isQuanpinSupported = false;

    private static String SPACE_HOLDER_FOR_NOT_MATCH = "^";

    private static char SPACE_HOLDER_FOR_NOT_MATCH_CHAR = '^';

    private static String SPACE_HOLDER_FOR_EMPTY = "%";

    private static char SPACE_HOLDER_FOR_EMPTY_CHAR = '%';

    private static String keys = "";

    private static PinyinMatcher pinyinMatcher;

    private static Handler myHandler;

    public static final int MSG_FOR_FILTERING_FINISHED = 1112;

    public static final int MSG_FOR_SHOW_ALL_LIST = MSG_FOR_FILTERING_FINISHED + 1;

    public static final int MSG_FOR_SHOW_NOTHING = MSG_FOR_FILTERING_FINISHED + 2;

    private static Contact[] result;

    private static List<Contact> resultList;

    private static List<Contact> allDisplayList;

    private static List<Contact> EMPTY_RESULT = new ArrayList<Contact>(0);

    private static int resultIndex = 0;

    private static boolean needIgnoreEmptyPhoneNumber = false;

    private ContactFilter() {
    };

    /**
     * 针对给定的拼音字母过虑通讯簿记录，程序算法如下：
     * 1、若输入的是0,1或特殊电话号话数字，诸如139、138、110、114等，则只匹配电话号码，不去匹配名字 <br>
     * 2、匹配电话号码 <br>
     * 3、匹配名字，如果设置了模糊匹配则支持模糊音匹配，过滤之后根据匹配的情况设置权重<br>
     * 
     * @param keys 用户输入的拼音字母（串）
     * @return ArrayList<Contact> 带有权重排序的联系人列表，
     *         包括的数据有用户名对应的全拼、用户电话号码、用户中文名以及命中位置的索引
     */
    private static int keysLen;

    public static List<Contact> doFilter(final String inputKeys) {
        needIgnoreEmptyPhoneNumber = false;
        return doFilterByCondition(inputKeys.trim());
    }

    public static List<Contact> doFilterIgnoreEmptyPhoneNumber(
            final String inputKeys) {
        needIgnoreEmptyPhoneNumber = false;
        return doFilterByCondition(inputKeys);
    }

    private static List<Contact> doFilterByCondition(final String inputKeys) {
        long start = System.currentTimeMillis();
        clearResultArray();
        if (null == inputKeys || inputKeys.length() < 1) {
            sendMessage(MSG_FOR_SHOW_ALL_LIST);
            return allDisplayList;
        }
        keys = inputKeys.toLowerCase();
        keysLen = keys.length();
        if (MAX_FILTER_LENGTH < keysLen) {
            sendMessage(MSG_FOR_SHOW_NOTHING);
            return EMPTY_RESULT;
        }
        doAllFilter(toBeFilterContactsArray);
        Arrays.sort(result, DisplayContactComparator.getInstance());
        setResultArrayToList();
        sendMessage(MSG_FOR_FILTERING_FINISHED);
        Log.i("total use:", String.valueOf(System.currentTimeMillis() - start));
        return resultList;
    }

    private static void clearResultArray() {
        for (int i = 0; i < result.length; i++) {
            result[i] = null;
        }
        resultIndex = 0;
    }

    private static void setResultArrayToList() {
        int len = result.length;
        if (null == resultList)
            resultList = new ArrayList<Contact>(len);
        else
            resultList.clear();
        for (int i = 0; i < len; i++) {
            Contact eachContact = result[i];
            if (null == eachContact)
                break;
            resultList.add(eachContact);
        }
    }

    private static void sendMessage(int msgType) {
        if (null == myHandler)
            return;
        Message message = new Message();
        if (msgType == MSG_FOR_FILTERING_FINISHED)
            message.what = MSG_FOR_FILTERING_FINISHED;
        else if (msgType == MSG_FOR_SHOW_ALL_LIST)
            message.what = MSG_FOR_SHOW_ALL_LIST;
        if (msgType == MSG_FOR_SHOW_NOTHING)
            message.what = MSG_FOR_SHOW_NOTHING;
        myHandler.sendMessageAtFrontOfQueue(message);
    }

    private static void doAllFilter(Contact[] contacts) {
        int contactCount = contacts.length;
        for (int i = 0; i < contactCount; i++) {
            Contact eachContact = contacts[i];
            if (needIgnoreEmptyPhoneNumber) {
                List<String> phoneList = eachContact.getPhoneList();
                if (phoneList.size() < 1)
                    continue;
            }
            doNameFilter(eachContact);
        }
    }

    private static List<String> filterKeys = new ArrayList<String>(10);

    private static String[][] pinyinArrays;

    private static String[][] pinyinNumberArray;

    private static int pinyinArrayLen;

    private static String leftKeys;

    private static void doNameFilter(Contact eachContact) {
        String[][] pinyinArray = eachContact.getNamePinyin();
        if (null == pinyinArray || pinyinArray.length < 1)
            return;
        leftKeys = keys;
        if (containChinese(keys)) {
            String name = eachContact.getFormatName();
            if (null != name && name.length() > 0) {
                int index = name.indexOf(keys);
                if (-1 != index) {
                    int score = calcChineseScore(index, name);
                    Contact resultContact = copyContactWithWeight(eachContact,
                            score);
                    result[resultIndex] = resultContact;
                    ++resultIndex;
                }
            }
            return;
        }
        prepareNamePinyinData(eachContact);

        if (keysLen > pinyinArrayLen) {
            // 如果输入的字符大于名字的字数，则直接匹配
            filterNameStepByStep(false);
        }
        if (leftKeys.length() > 0) {
            filterNameStepByStep(true);
        }
        if (leftKeys.length() < 1 && filterKeys.size() == pinyinArrayLen) {
            updateContactResult(filterKeys, eachContact);
        }
    }

    private static int calcChineseScore(int index, String name) {
        int score = 0;
        if (0 == index)
            score += LASTNAME_MATCH_WEIGHT;
        int nameLen = name.length();
        if (keysLen == nameLen)
            score += WHOLE_WORD_MATCH_WEIGHT;
        score -= nameLen * NAME_LENGTH_WEIGHT;
        return score;
    }

    private static boolean containChinese(final String destStrings) {
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher matcher = pattern.matcher(destStrings);
        return matcher.find();
    }

    private static void prepareNamePinyinData(Contact eachContact) {
        String[][] originalPinyinArray = eachContact.getNamePinyin();
        pinyinArrayLen = originalPinyinArray.length;
        pinyinArrays = new String[pinyinArrayLen][];
        pinyinNumberArray = new String[pinyinArrayLen][];
        for (int i = 0; i < pinyinArrayLen; i++) {
            String[] eachPinyinArray = originalPinyinArray[i];
            int eachPinyinLen = eachPinyinArray.length;
            String[] eachPinyinArrayCopy = new String[eachPinyinLen];
            System.arraycopy(eachPinyinArray, 0, eachPinyinArrayCopy, 0,
                    eachPinyinLen);
            pinyinArrays[i] = eachPinyinArrayCopy;
        }
    }

    private static int currentNameWordPos;

    private static void filterNameStepByStep(boolean byStep) {
        filterKeys.clear();
        leftKeys = keys;
        for (currentNameWordPos = 0; currentNameWordPos < pinyinArrayLen; currentNameWordPos++) {
            if (leftKeys.length() < 1) {
                int filterKeySize = filterKeys.size();
                // 已匹配上，未匹配的位置补上空字符，方便计算
                for (; filterKeySize < pinyinArrayLen; filterKeySize++) {
                    filterKeys.add(SPACE_HOLDER_FOR_EMPTY);
                }
                break;
            }
            String[] multiNamePinyin = pinyinArrays[currentNameWordPos];
            hitStr = "";
            fuzzyHitStr = "";
            // 组合而来的新字符匹配成功的标志
            rematch = false;
            if (byStep)
                filterEachNameWordStepByStep(multiNamePinyin);
            else
                filterEachNameWordByWholeWord(multiNamePinyin);
            // 保存命中部分
            saveHitResult();
        }
    }

    private static String hitStr;

    private static String fuzzyHitStr;

    private static boolean rematch;

    private static String matchedStr;

    private static void filterEachNameWordStepByStep(String[] multiNamePinyin) {
        int multiNamePinyinLen = multiNamePinyin.length;
        char currentCharToMatch = leftKeys.charAt(0);
        for (int j = 0; j < multiNamePinyinLen; j++) {
            hitStr = "";
            String eachNamePinyin = multiNamePinyin[j];
            matchedStr = "";
            boolean isFuzzyMatch = false;
            boolean matchAllLeft = false;
            if (currentNameWordPos < pinyinArrayLen - 1
                    || leftKeys.length() == 1) {
                isFuzzyMatch = doFuzzyMatchWithChar(eachNamePinyin,
                        currentCharToMatch);
            } else {
                // 最后一个字母时，与剩下来的字符进行匹配
                isFuzzyMatch = doFuzzyMatchWithString(eachNamePinyin,
                        leftKeys.toCharArray());
                if (isFuzzyMatch)
                    matchAllLeft = true;
            }
            if (isFuzzyMatch) {
                hitStr = matchAllLeft ? leftKeys : String
                        .valueOf(currentCharToMatch);
                fuzzyHitStr = matchedStr;
            } else if (!matchAllLeft) {
                int filterKeySize = filterKeys.size();
                if (filterKeySize > 0 && currentNameWordPos > 0
                        && j == multiNamePinyinLen - 1) {
                    matchCombinedWithLastHitChar(filterKeySize);
                }
            }
            if (isFuzzyMatch && !rematch) {
                pinyinArrays[currentNameWordPos] = new String[] {
                        eachNamePinyin
                };
                break;
            }
        }
    }

    private static void filterEachNameWordByWholeWord(String[] multiNamePinyin) {
        int multiNamePinyinLen = multiNamePinyin.length;
        for (int j = 0; j < multiNamePinyinLen; j++) {
            hitStr = "";
            String eachNamePinyin = multiNamePinyin[j];
            String strToWholeWord;
            int leftKeysLen = leftKeys.length();
            int eachNamePinyinLen = eachNamePinyin.length();
            if (leftKeysLen >= eachNamePinyinLen) {
                strToWholeWord = leftKeys.substring(0, eachNamePinyinLen);
            } else {
                strToWholeWord = leftKeys.substring(0, leftKeysLen);
            }
            matchedStr = "";
            boolean isFuzzyMatch = false;
            if (strToWholeWord.length() > 1) {
                isFuzzyMatch = doFuzzyMatchWithString(eachNamePinyin,
                        strToWholeWord.toCharArray());
            } else {
                isFuzzyMatch = doFuzzyMatchWithChar(eachNamePinyin,
                        strToWholeWord.charAt(0));
            }
            if (isFuzzyMatch) {
                hitStr = strToWholeWord;
                fuzzyHitStr = matchedStr;
                pinyinArrays[currentNameWordPos] = new String[] {
                        eachNamePinyin
                };
                break;
            }
        }
    }

    private static void matchCombinedWithLastHitChar(int filterKeySize) {
        // 与前一个字符组合看是否能与前一个音节匹配
        char currentCharToMatch = leftKeys.charAt(0);
        int preNameWordPos = currentNameWordPos - 1;
        String preNamePinyin = pinyinArrays[preNameWordPos][0];
        String lastHitStr = filterKeys.get(filterKeySize - 1);
        char lastHitChar = lastHitStr.charAt(0);
        if (lastHitChar != SPACE_HOLDER_FOR_NOT_MATCH_CHAR
                && lastHitChar != SPACE_HOLDER_FOR_EMPTY_CHAR) {
            int len = lastHitStr.length();
            char[] combinedChar = new char[len + 1];
            System.arraycopy(lastHitStr.toCharArray(), 0, combinedChar, 0, len);
            combinedChar[len] = currentCharToMatch;
            boolean isFuzzyMatch = doFuzzyMatchWithString(preNamePinyin,
                    combinedChar);
            if (isFuzzyMatch) {
                hitStr = String.valueOf(currentCharToMatch);
                fuzzyHitStr = matchedStr;
                filterKeys.remove(filterKeySize - 1);
                rematch = true;
                currentNameWordPos--;
            } else {
                // 跟当前音节进行匹配
                preNameWordPos++;
                String currentNamePinyin = pinyinArrays[preNameWordPos][0];
                isFuzzyMatch = doFuzzyMatchWithString(currentNamePinyin,
                        combinedChar);
                if (isFuzzyMatch) {
                    hitStr = String.valueOf(currentCharToMatch);
                    fuzzyHitStr = matchedStr;
                    filterKeys.remove(filterKeySize - 1);
                    filterKeys.add(SPACE_HOLDER_FOR_NOT_MATCH);
                    rematch = true;
                }
            }
        }
    }

    private static void saveHitResult() {
        int hitStrLen = hitStr.length();
        if (hitStrLen > 0) {
            filterKeys.add(fuzzyHitStr);
            int leftKeysLen = leftKeys.length();
            if (leftKeysLen > 0)
                leftKeys = leftKeys.substring(hitStrLen);
        } else {
            filterKeys.add(SPACE_HOLDER_FOR_NOT_MATCH);
        }
    }

    private static boolean doFuzzyMatchWithString(final String name,
            char[] strToMatch) {
        return pinyinMatcher.doMatchForString(name, strToMatch);
    }

    private static boolean doFuzzyMatchWithChar(final String name,
            char charToMatch) {
        return pinyinMatcher.doMatchForSingleChar(name, charToMatch);
    }

    /**
     * 模糊匹配
     */
    private static void initPinyinMatcher() {
        pinyinMatcher = QuanPinMatcher.getInstance();
        pinyinMatcher.setIsFuzzyPinYinSupported(isFuzzyPinYinSupported);

    }

    /**
     * 更新匹配后的结果
     */
    private static void updateContactResult(List<String> filterKeys,
            Contact contactBeforeWeight) {
        Integer[][] highLightIndexs = new Integer[2][];
        int score = 0;
        boolean isEachFirstLetterMatch = true;
        boolean isLastNameMatch = false;
        boolean isNotBreakMatch = true;
        int hitCount = 0;
        int filterKeyCount = filterKeys.size();
        Integer[] index = new Integer[filterKeyCount];
        Arrays.fill(index, 0);
        Integer[] indexLen = new Integer[filterKeyCount];
        for (int j = 0; j < filterKeyCount; j++) {
            String filterKey = filterKeys.get(j);
            char firstCharInFilterKey = filterKey.charAt(0);
            boolean isFilterKeyHasNotMatchHolder = (firstCharInFilterKey == SPACE_HOLDER_FOR_NOT_MATCH_CHAR) ? true
                    : false;
            boolean isFilterKeyHasEmptyHolder = (firstCharInFilterKey == SPACE_HOLDER_FOR_EMPTY_CHAR) ? true
                    : false;

            indexLen[j] = (isFilterKeyHasNotMatchHolder || isFilterKeyHasEmptyHolder) ? 0
                    : filterKey.length();
            String nameWord;
            String familyName;
            if (isQuanpinSupported) {
                nameWord = pinyinArrays[j][0];
                familyName = pinyinArrays[0][0];
            } else {
                nameWord = pinyinNumberArray[j][0];
                familyName = pinyinNumberArray[0][0];
            }
            char firstFamilyNameChar = familyName.charAt(0);
            char firstCharInName = nameWord.charAt(0);

            if (!isFilterKeyHasEmptyHolder && !isFilterKeyHasNotMatchHolder) {
                score += NORMAL_MATCH_WEIGHT;
                hitCount++;
                // 整字匹配
                if (nameWord.length() == filterKey.length())
                    score += WHOLE_WORD_MATCH_WEIGHT;
            }
            // 中间没有断
            if (isFilterKeyHasNotMatchHolder) {
                isNotBreakMatch = false;
                isEachFirstLetterMatch = false;
            }
            // 每字一个拼音首字母
            if (isEachFirstLetterMatch && !isFilterKeyHasEmptyHolder
                    && firstCharInName != firstCharInFilterKey)
                isEachFirstLetterMatch = false;
            // 匹配到姓额外加分
            if (j == 0 && firstFamilyNameChar == filterKey.charAt(0))
                isLastNameMatch = true;
        }
        if (isEachFirstLetterMatch && hitCount < pinyinArrayLen
                && hitCount < keysLen)
            isEachFirstLetterMatch = false;
        score += isEachFirstLetterMatch ? EACH_FIRST_LETER_MATCH_WEIGHT : 0;
        score += isLastNameMatch ? LASTNAME_MATCH_WEIGHT : 0;
        score += isNotBreakMatch ? NOTBREAK_MATCH_WEIGHT : 0;
        score -= pinyinArrayLen * NAME_LENGTH_WEIGHT;
        highLightIndexs[0] = index;
        highLightIndexs[1] = indexLen;
        Contact cf = copyContactWithWeight(contactBeforeWeight, score);
        result[resultIndex] = cf;
        ++resultIndex;
        return;
    }

    private static Contact copyContactWithWeight(Contact contactBeforeWeight,
            int weight) {
        Contact cf = new Contact();
        long phoneCid = contactBeforeWeight.getPhoneCid();
        String name = contactBeforeWeight.getFormatName();
        cf.setUid(contactBeforeWeight.getUid());
        cf.setContactId(contactBeforeWeight.getContactId());
        cf.setPrimePhoneNumber(contactBeforeWeight.getPrimePhoneNumber());
        cf.setPhoneCid(phoneCid);
        cf.setFormatName(name);
        cf.setWeight(weight);
        cf.setAvatar(contactBeforeWeight.getAvatar());
        return cf;
    }

    /**
     * 将对应的字母转换成T9对应的数字 异常字符全部当成0处理
     * 
     * @param pinyin
     * @return 对应字母所在T9上的数字
     */
    public static String getKeyNumWithString(final String pinyin) {
        char[] keyNum = pinyin.toCharArray();
        for (int j = 0; j < keyNum.length; j++) {
            char ch = Character.toLowerCase(keyNum[j]);
            if ((ch >= '0' && ch <= '9') || ch == wordBreakerChar
                    || ch == multiSyllaBreakerChar) {
                keyNum[j] = ch;
                continue;
            }
            if (ch >= 'a' && ch <= 'z') {
                if (ch < 's')
                    keyNum[j] = (char)(((ch - 'a') / 3) + '2');
                else if (ch == 's')
                    keyNum[j] = '7';
                else if (ch <= 'v')
                    keyNum[j] = '8';
                else
                    keyNum[j] = '9';
            } else
                keyNum[j] = '0';
        }
        return String.copyValueOf(keyNum);
    }

    public static char getKeyNumWithChar(final char pinyinChar) {
        if ((pinyinChar >= '0' && pinyinChar <= '9')
                || pinyinChar == wordBreakerChar
                || pinyinChar == multiSyllaBreakerChar) {
            return pinyinChar;
        }
        if (pinyinChar >= 'a' && pinyinChar <= 'z') {
            if (pinyinChar < 's')
                return (char)(((pinyinChar - 'a') / 3) + '2');
            else if (pinyinChar == 's')
                return '7';
            else if (pinyinChar <= 'v')
                return '8';
            else
                return '9';
        }
        return '0';
    }

    /**
     * 获取T9键盘数字对应的字母
     * 
     * @param keyNum
     * @return 给定数字所在T9键盘上对应的字母
     */
    public static char[] getCharFromT9Num(char keyNum) {
        switch (keyNum) {
            case 0:
                return new char[] {
                        '0'
                };
            case 1:
                return new char[] {
                        '1'
                };
            case 2:
                return new char[] {
                        'a', 'b', 'c'
                };
            case 3:
                return new char[] {
                        'd', 'e', 'f'
                };
            case 4:
                return new char[] {
                        'g', 'h', 'i'
                };
            case 5:
                return new char[] {
                        'j', 'k', 'l'
                };
            case 6:
                return new char[] {
                        'm', 'n', 'o'
                };
            case 7:
                return new char[] {
                        'p', 'q', 'r', 's'
                };
            case 8:
                return new char[] {
                        't', 'u', 'v'
                };
            case 9:
                return new char[] {
                        'x', 'y', 'z'
                };
        }
        return new char[0];
    }

    public static void setFuzzyPinYinSupported(boolean isFuzzyPinYinSupported) {
        ContactFilter.isFuzzyPinYinSupported = isFuzzyPinYinSupported;
        initPinyinMatcher();
    }

    public static void setQuanpinSupported(boolean isQuanpinSupported) {
        ContactFilter.isQuanpinSupported = isQuanpinSupported;
        initPinyinMatcher();
    }

    public static void setWordBreaker(String wordBreaker) {
        ContactFilter.wordBreaker = wordBreaker;
        ContactFilter.wordBreakerChar = wordBreaker.charAt(0);
    }

    public static void setMultiSyllaBreaker(String multiSyllaBreaker) {
        ContactFilter.multiSyllaBreaker = multiSyllaBreaker;
        ContactFilter.multiSyllaBreakerChar = multiSyllaBreaker.charAt(0);
    }

    /**
     * 设置需要过滤的联系人列表
     * 
     * @param contactsList
     */
    public static void setToBeFilteredContactsList(List<Contact> contactsList) {
        int contactsCount = 0;
        if (null != contactsList) {
            contactsCount = contactsList.size();
            toBeFilterContactsArray = new Contact[contactsCount];
            contactsList.toArray(toBeFilterContactsArray);
        } else {
            toBeFilterContactsArray = new Contact[0];
        }
        result = new Contact[contactsCount];
        allDisplayList = Arrays.asList(toBeFilterContactsArray);
    }

    public static void setHandler(Handler handler) {
        myHandler = handler;
    }

    public static List<Contact> getResultList() {
        return resultList;
    }

    public static void setMatchedStr(String matchedStr) {
        ContactFilter.matchedStr = matchedStr;
    }

    static class DisplayContactComparator implements Comparator<Contact> {

        private static DisplayContactComparator comparator = null;

        public static DisplayContactComparator getInstance() {
            if (null == comparator) {
                comparator = new DisplayContactComparator();
            }
            return comparator;
        }

        @Override
        public int compare(Contact result1, Contact result2) {
            if (null == result1 || null == result2)
                return 0;
            int weight1 = result1.getWeight();
            int weight2 = result2.getWeight();
            if (weight1 > weight2) {
                return -1;
            } else if (weight1 < weight2) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
