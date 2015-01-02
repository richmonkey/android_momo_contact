
package cn.com.nd.momo.api.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 汉字转拼音，包括多音字的处理
 * 
 * @author chenjp
 */
public class PinyinHelper {
    public static String[] pinyinArray = null;

    private static char commaBreaker = ',';

    // ox4e00 ,十进制为19968
    private static int chUnicodePart1Begin = 19968;

    // 0x5e67,24167
    private static int chUnicodePart2Begin = 24167;

    // 0x6ECF,28367
    private static int chUnicodePart3Begin = 28367;

    // 0x7F37,32567
    private static int chUnicodePart4Begin = 32567;

    // 0x8F9F,36767
    private static int chUnicodePart5Begin = 36767;

    // 0x9FA5,40869
    private static int chUnicodePart5End = 40869;

    private PinyinHelper() {
    };

    /**
     * 汉字转成拼音，如果是数字则返回数字，其他字符直接忽略
     * 
     * @param chinese
     * @return 对应的拼音或数字
     */
    public static String convertChineseToPinyin(final String chinese) {
        StringBuilder result = new StringBuilder();
        int len = chinese.length();
        for (int i = 0; i < len; i++) {
            char eachChar = chinese.charAt(i);
            int unicodeValue = chinese.charAt(i);
            if (unicodeValue >= chUnicodePart1Begin
                    && unicodeValue < chUnicodePart2Begin) {
                int offset = unicodeValue - chUnicodePart1Begin;
                result.append(MultiPinYin1.pinyinArray[offset]);
                continue;
            } else if (unicodeValue >= chUnicodePart2Begin
                    && unicodeValue < chUnicodePart3Begin) {
                int offset = Integer
                        .valueOf(unicodeValue - chUnicodePart2Begin);
                result.append(MultiPinYin2.pinyinArray[offset]);
                continue;
            } else if (unicodeValue >= chUnicodePart3Begin
                    && unicodeValue < chUnicodePart4Begin) {
                int offset = unicodeValue - chUnicodePart3Begin;
                result.append(MultiPinYin3.pinyinArray[offset]);
                continue;
            } else if (unicodeValue >= chUnicodePart4Begin
                    && unicodeValue < chUnicodePart5Begin) {
                int offset = unicodeValue - chUnicodePart4Begin;
                result.append(MultiPinYin4.pinyinArray[offset]);
                continue;
            } else if (unicodeValue >= chUnicodePart5Begin
                    && unicodeValue <= chUnicodePart5End) {
                int offset = unicodeValue - chUnicodePart5Begin;
                result.append(MultiPinYin5.pinyinArray[offset]);
                continue;
            } else if (isAlphaPhebicNumeric(eachChar)) {
                result.append(eachChar);
                while (i < len - 1) {
                    i++;
                    char nextChar = chinese.charAt(i);
                    if (isAlphaPhebicNumeric(nextChar))
                        result.append(nextChar);
                    else {
                        i--;
                        break;
                    }
                }
            }
        }
        return result.toString();
    }

    // TODO 需要重构
    public static String[][] convertChineseToPinyinArray(final String chinese) {
        List<String[]> pinyinList = new ArrayList<String[]>();
        int len = chinese.length();
        for (int i = 0; i < len; i++) {
            char eachChar = chinese.charAt(i);
            int unicodeValue = chinese.charAt(i);
            if (unicodeValue >= chUnicodePart1Begin
                    && unicodeValue < chUnicodePart2Begin) {
                int offset = unicodeValue - chUnicodePart1Begin;
                String[] resultPinyinArray = convertMultiPinStringToArray(MultiPinYin1.pinyinArray[offset]);
                pinyinList.add(resultPinyinArray);
                continue;
            } else if (unicodeValue >= chUnicodePart2Begin
                    && unicodeValue < chUnicodePart3Begin) {
                int offset = Integer
                        .valueOf(unicodeValue - chUnicodePart2Begin);
                String[] resultPinyinArray = convertMultiPinStringToArray(MultiPinYin2.pinyinArray[offset]);
                pinyinList.add(resultPinyinArray);
                continue;
            } else if (unicodeValue >= chUnicodePart3Begin
                    && unicodeValue < chUnicodePart4Begin) {
                int offset = unicodeValue - chUnicodePart3Begin;
                String[] resultPinyinArray = convertMultiPinStringToArray(MultiPinYin3.pinyinArray[offset]);
                pinyinList.add(resultPinyinArray);
                continue;
            } else if (unicodeValue >= chUnicodePart4Begin
                    && unicodeValue < chUnicodePart5Begin) {
                int offset = unicodeValue - chUnicodePart4Begin;
                String[] resultPinyinArray = convertMultiPinStringToArray(MultiPinYin4.pinyinArray[offset]);
                pinyinList.add(resultPinyinArray);
                continue;
            } else if (unicodeValue >= chUnicodePart5Begin
                    && unicodeValue <= chUnicodePart5End) {
                int offset = unicodeValue - chUnicodePart5Begin;
                String[] resultPinyinArray = convertMultiPinStringToArray(MultiPinYin5.pinyinArray[offset]);
                pinyinList.add(resultPinyinArray);
                continue;
            }

            if (isAlphaPhebicNumeric(eachChar)) {
                StringBuilder result = new StringBuilder();
                result.append(eachChar);
                while (i < len - 1) {
                    i++;
                    char nextChar = chinese.charAt(i);
                    if (isAlphaPhebicNumeric(nextChar))
                        result.append(nextChar);
                    else {
                        i--;
                        break;
                    }
                }
                if (result.length() > 0) {
                    String[] resultPinyinArray = new String[] {
                            result.toString()
                    };
                    pinyinList.add(resultPinyinArray);
                }
            } else {
                pinyinList.add(new String[] {
                        String.valueOf(eachChar)
                });
            }
        }
        return pinyinList.toArray(new String[pinyinList.size()][]);
    }

    private static String[] convertMultiPinStringToArray(final String multiPinyin) {
        List<String> matchList = new ArrayList<String>();
        int len = multiPinyin.length();
        int i = 0, start = 0;
        while (i < len) {
            if (multiPinyin.charAt(i) == commaBreaker) {
                String subStr = multiPinyin.substring(start, i);
                if (subStr.length() > 0)
                    matchList.add(subStr);
                start = ++i;
                continue;
            }
            i++;
        }
        if (start < len) {
            String subStr = multiPinyin.substring(start, i);
            if (subStr.length() > 0)
                matchList.add(subStr);
        }
        return matchList.toArray(new String[matchList.size()]);
    }

    public static boolean isAlphaPhebicNumeric(char cs) {
        if (cs >= '0' && cs <= '9')
            return true;
        if (cs >= 'a' && cs <= 'z')
            return true;
        if (cs >= 'A' && cs <= 'Z')
            return true;
        return false;
    }

    public static void initializeResource() {
        try {
            FileReader in = new FileReader("d:/unicode_to_hanyu_pinyin.txt");
            BufferedReader br = new BufferedReader(in);

            FileWriter fw = new FileWriter(
                    "d:/unicode_to_hanyu_pinyin_result.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            String temp = "";
            while (null != (temp = br.readLine())) {
                StringBuilder sb = new StringBuilder("\"");
                temp = Utils.replaceChars(temp, "012345", "");
                if (temp.contains(",")) {
                    String[] split = temp.split(",");
                    for (int i = 0; i < split.length; i++) {
                        if (-1 == sb.indexOf(split[i]))
                            sb.append(split[i]).append(commaBreaker);
                    }
                } else {
                    sb.append(temp);
                }
                if (-1 != sb.indexOf(String.valueOf(commaBreaker))) {
                    if (commaBreaker == sb.charAt(sb.length() - 1))
                        sb.deleteCharAt(sb.length() - 1);
                }
                sb.append("\"").append(",").append("\n");
                bw.write(sb.toString());
            }

            bw.close();
            br.close();

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
