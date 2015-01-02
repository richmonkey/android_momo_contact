
package cn.com.nd.momo.api.util;

import java.util.Comparator;

import cn.com.nd.momo.api.types.Contact;

public class PinYinComparator implements Comparator<Contact> {

    private static PinYinComparator comparator = null;

    public static PinYinComparator getInstance() {
        if (null == comparator) {
            comparator = new PinYinComparator();
        }
        return comparator;
    }

    @Override
    public int compare(Contact contact1, Contact contact2) {
        String namePinyin1 = convertPinyinArrayToString(contact1);
        String namePinyin2 = convertPinyinArrayToString(contact2);
        return namePinyin1.compareToIgnoreCase(namePinyin2);
    }

    private String convertPinyinArrayToString(Contact dc) {
        String[][] pinyinArray = dc.getNamePinyin();
        StringBuilder sb = new StringBuilder(20);
        if (null != pinyinArray) {
            for (int i = 0; i < pinyinArray.length; i++) {
                String[] eachPinyin = pinyinArray[i];
                if (null != eachPinyin && eachPinyin.length > 0)
                    sb.append(eachPinyin[0]);
            }
        }
        return sb.toString();
    }
}
