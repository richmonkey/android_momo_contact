
package cn.com.nd.momo.api.parsers.json;

import android.text.TextUtils;

import java.util.List;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.types.Address;
import cn.com.nd.momo.api.types.Avatar;
import cn.com.nd.momo.api.types.Contact;
import cn.com.nd.momo.api.types.Weibo;

public class ContactParser extends AbstractParser<Contact> {
    private static final String KEY_USER_ID = "user_id";

    private static final String KEY_NAME = "name";

    private static final String KEY_NOTE = "note";

    private static final String KEY_ORGANIZATION = "organization";

    private static final String KEY_WEIBO_URLS = "urls";

    private static final String KEY_USER_LINK = "user_link";

    private static final String KEY_IN_MY_CONTACT = "in_my_contact";

    private static final String KEY_EMAILS = "emails";

    private static final String KEY_TELS = "tels";

    private static final String KEY_USER_STATUS = "user_status";

    private static final String KEY_COMPLETED = "completed";

    private static final String KEY_SEND_CARD_COUNT = "send_card_count";

    private static final String KEY_GENDER = "gender";

    private static final String KEY_BIRTHDAY = "birthday";

    private static final String KEY_IS_HIDE_YEAR = "is_hide_year";

    private static final String KEY_LUNAR_BDAY = "lunar_bday";

    private static final String KEY_IS_LUNAR = "is_lunar";

    private static final String KEY_ANIMAL_SIGN = "animal_sign";

    private static final String KEY_ZODIAC = "zodiac";

    private static final String KEY_RESIDENCE = "residence";

    //private static final String KEY_AVATAR = "avatar";
    private static final String KEY_AVATAR_B64 = "avatar_b64";

    private static final String KEY_ITEM_TYPE = "type";

    private static final String KEY_ITEM_VALUE = "value";

    private static final String KEY_PHONE_ITEM_PREF = "pref";

    // --------------联系人特有的键名
    private static final String KEY_ID = "id";

    private static final String KEY_FAMILY_NAME = "family_name";

    private static final String KEY_GIVEN_NAME = "given_name";

    private static final String KEY_FORMATTED_NAME = "formatted_name";

    private static final String KEY_NICKNAME = "nickname";

    private static final String KEY_DEPARTMENT = "department";

    private static final String KEY_TITLE = "title";

    private static final String KEY_MODIFIED_AT = "modified_at";

    private static final String KEY_ADDRESSES = "addresses";

    private static final String KEY_IMS = "ims";

    private static final String KEY_EVENTS = "events";

    private static final String KEY_URLS = "urls";

    private static final String KEY_RELATIONS = "relations";

    @Override
    public Contact parse(JSONObject json) throws JSONException {
        Contact contact = new Contact();
        contact.setUid(json.optLong(KEY_USER_ID));
        contact.setName(json.optString(KEY_NAME));
        contact.setGender(json.optInt(KEY_GENDER));
        contact.setAnimalSign(json.optString(KEY_ANIMAL_SIGN));
        contact.setZodiac(json.optString(KEY_ZODIAC));
        contact.setResidence(json.optString(KEY_RESIDENCE));
        contact.setNote(json.optString(KEY_NOTE));
        contact.setOrganization(json.optString(KEY_ORGANIZATION));

        String avatarB64 = json.optString(KEY_AVATAR_B64);
        if (!TextUtils.isEmpty(avatarB64)) {
            byte[] image = Base64.decode(avatarB64, Base64.DEFAULT);
            Avatar avatar = new Avatar(-1, null, image);
            contact.setAvatar(avatar);
        }

        if (!json.isNull(KEY_WEIBO_URLS)) {
            JSONArray weiboArray = json.optJSONArray(KEY_WEIBO_URLS);
            for (int i = 0; i < weiboArray.length(); ++i) {
                JSONObject object = weiboArray.optJSONObject(i);
                String weiboType = object.optString(KEY_ITEM_TYPE);
                String weiboValue = object.optString(KEY_ITEM_VALUE);
                contact.getWebsiteLabelList().add(weiboType);
                contact.getWebsiteList().add(weiboValue);
                Weibo weibo = new Weibo(weiboType, weiboValue);
                contact.getWeiboList().add(weibo);
            }
        }
        contact.setUserLink(json.optInt(KEY_USER_LINK));
        contact.setInMyContacts(json.optBoolean(KEY_IN_MY_CONTACT));
        if (!json.isNull(KEY_EMAILS)) {
            JSONArray emailsArray = json.optJSONArray(KEY_EMAILS);
            for (int i = 0; i < emailsArray.length(); ++i) {
                JSONObject email = emailsArray.optJSONObject(i);
                String emailLabel = email.optString(KEY_ITEM_TYPE);
                String emailValue = email.optString(KEY_ITEM_VALUE);
                contact.getEmailLabelList().add(emailLabel);
                contact.getEmailList().add(emailValue);
            }
        }
        if (!json.isNull(KEY_TELS)) {
            JSONArray telsArray = json.optJSONArray(KEY_TELS);
            for (int i = 0; i < telsArray.length(); ++i) {
                JSONObject tel = telsArray.optJSONObject(i);
                String teLabel = tel.optString(KEY_ITEM_TYPE);
                String telValue = tel.optString(KEY_ITEM_VALUE);
                boolean telPref = tel.optBoolean(KEY_PHONE_ITEM_PREF);
                if (telPref) {
                    contact.getPhoneLabelList().add(0, teLabel);
                    contact.getPhoneList().add(0, telValue);
                    contact.setPrimePhoneNumber(telValue);
                    contact.getPrefPhoneList().add(0, telPref);
                } else {
                    contact.getPhoneLabelList().add(teLabel);
                    contact.getPhoneList().add(telValue);
                    contact.getPrefPhoneList().add(telPref);
                }
            }
        }
        if (!json.isNull(KEY_BIRTHDAY)) {
            String birthday = json.optString(KEY_BIRTHDAY);
            if (null != birthday && birthday.length() > 0) {
                contact.setBirthday(birthday);
            }
        }
        if (!json.isNull(KEY_IS_HIDE_YEAR)) {
            contact.setIsHideYear(json.optBoolean(KEY_IS_HIDE_YEAR));
        }
        if (!json.isNull(KEY_LUNAR_BDAY)) {
            contact.setLunarBirthDay(json.optString(KEY_LUNAR_BDAY));
        }
        if (!json.isNull(KEY_IS_LUNAR)) {
            contact.setNeedLunarBirthDay(json.optBoolean(KEY_IS_LUNAR));
        }
        if (!json.isNull(KEY_USER_STATUS)) {
            contact.setUserStatus(json.optInt(KEY_USER_STATUS));
        }
        if (!json.isNull(KEY_COMPLETED)) {
            contact.setCompleted(json.optInt(KEY_COMPLETED));
        }
        if (!json.isNull(KEY_SEND_CARD_COUNT)) {
            contact.setSendCardCount(json.optInt(KEY_SEND_CARD_COUNT));
        }
        return contact;
    }

    public Contact parseContact(JSONObject json) throws JSONException {
        Contact contact = new Contact();
        contact.setContactId(json.getLong(KEY_ID));
        contact.setFormatName(json.getString(KEY_FORMATTED_NAME));
        contact.setLastName(json.getString(KEY_FAMILY_NAME));
        contact.setFirstName(json.getString(KEY_GIVEN_NAME));
        contact.setNickName(json.getString(KEY_NICKNAME));
        contact.setBirthday(json.getString(KEY_BIRTHDAY));
        String avatarB64 = json.optString(KEY_AVATAR_B64);
        if (!TextUtils.isEmpty(avatarB64)) {
            byte[] image = Base64.decode(avatarB64, Base64.DEFAULT);
            Avatar avatar = new Avatar(-1, null, image);
            contact.setAvatar(avatar);
        }
        contact.setOrganization(json.getString(KEY_ORGANIZATION));
        contact.setDepartment(json.getString(KEY_DEPARTMENT));
        contact.setJobTitle(json.getString(KEY_TITLE));
        contact.setNote(json.getString(KEY_NOTE));
        contact.setModifyDate(json.optLong(KEY_MODIFIED_AT));

        JSONArray emailArray = json.getJSONArray("emails");
        List<List<String>> emailsList = contact.convertJsonArrayToFieldList(emailArray);
        if (emailsList.size() > 0) {
            contact.setEmailLabelList(emailsList.get(0));
            contact.setEmailList(emailsList.get(1));
        }

        JSONArray telsArray = json.getJSONArray("tels");
        List<List<String>> telsList = contact.convertJsonArrayToFieldList(telsArray);
        if (telsList.size() > 0) {
            List<String> telVauleList = telsList.get(1);
            if (telVauleList.size() > 0) {
                contact.setPhoneLabelList(telsList.get(0));
                contact.setPhoneList(telVauleList);
                List<String> prefList = telsList.get(2);
                if (null != prefList && prefList.size() > 0) {
                    for (String pref : prefList) {
                        contact.getPrefPhoneList().add(pref.equals("1") ? true : false);
                    }
                }
            }
        }
        JSONArray urlsArray = json.getJSONArray("urls");
        List<List<String>> urlsList = contact.convertJsonArrayToFieldList(urlsArray);
        if (urlsList.size() > 0) {
            contact.setWebsiteLabelList(urlsList.get(0));
            contact.setWebsiteList(urlsList.get(1));
        }

        JSONArray imsArray = json.getJSONArray("ims");
        List<List<String>> imsList = contact.convertJsonArrayToFieldList(imsArray);
        List<String> imValueList = imsList.get(1);
        if (imValueList.size() > 0) {
            contact.setImLabelList(imsList.get(0));
            contact.setImList(imValueList);
            contact.setImProtocolList(imsList.get(2));
        }
        JSONArray eventsArray = json.getJSONArray("events");
        List<List<String>> eventsList = contact.convertJsonArrayToFieldList(eventsArray);
        if (eventsList.size() > 0) {
            contact.setEventLabelList(eventsList.get(0));
            contact.setEventList(eventsList.get(1));
        }
        JSONArray relationsArray;

        relationsArray = json.getJSONArray("relations");

        List<List<String>> relationsList = contact.convertJsonArrayToFieldList(relationsArray);
        if (relationsList.size() > 0) {
            contact.setRelationLabelList(relationsList.get(0));
            contact.setRelationList(relationsList.get(1));
        }
        JSONArray addressArray = json.getJSONArray("addresses");
        List<Address> addressList = contact.convertJsonArrayToAddressList(addressArray);
        if (addressList.size() > 0) {
            contact.setAddressList(addressList);
        }

        return contact;
    }

    @Override
    public JSONObject toJSONObject(Contact t) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(KEY_USER_ID, t.getUid());
        json.put(KEY_NAME, t.getName());
        json.put(KEY_GENDER, t.getGender());
        json.put(KEY_ANIMAL_SIGN, t.getAnimalSign());
        json.put(KEY_ZODIAC, t.getZodiac());
        json.put(KEY_RESIDENCE, t.getResidence());
        json.put(KEY_NOTE, t.getNote());
        json.put(KEY_ORGANIZATION, t.getOrganization());

        Avatar avatar = t.getAvatar();
        if (avatar != null) {
            byte[] image = avatar.getMomoAvatarImage();
            if (image != null && image.length > 0) {
                String avatarB64 = Base64.encodeToString(image, Base64.DEFAULT);
                json.put(KEY_AVATAR_B64, avatarB64);
            }
        }

        // 微博列表
        if (t.getWebsiteList() != null) {
            JSONArray urlList = new JSONArray();
            for (int i = 0; i < t.getWebsiteList().size(); i++) {
                JSONObject url = new JSONObject();
                url.put(KEY_ITEM_TYPE, t.getWebsiteLabelList().get(i));
                url.put(KEY_ITEM_VALUE, t.getWebsiteList().get(i));

                urlList.put(url);
            }
            json.put(KEY_WEIBO_URLS, urlList);
        }
        json.put(KEY_USER_LINK, t.getUserLink());
        json.put(KEY_IN_MY_CONTACT, t.isInMyContacts() ? true : false);

        // 邮箱列表
        if (t.getEmailLabelList() != null) {
            JSONArray emailList = new JSONArray();
            for (int i = 0; i < t.getEmailLabelList().size(); i++) {
                JSONObject email = new JSONObject();
                email.put(KEY_ITEM_TYPE, t.getEmailLabelList().get(i));
                email.put(KEY_ITEM_VALUE, t.getEmailList().get(i));

                emailList.put(email);
            }
            json.put(KEY_EMAILS, emailList);
        }

        // 电话列表
        if (t.getPhoneLabelList() != null) {
            JSONArray telList = new JSONArray();
            for (int i = 0; i < t.getPhoneLabelList().size(); i++) {
                JSONObject tel = new JSONObject();
                tel.put(KEY_ITEM_TYPE, t.getPhoneLabelList().get(i));
                tel.put(KEY_ITEM_VALUE, t.getPhoneList().get(i));
                tel.put(KEY_PHONE_ITEM_PREF, t.getPrefPhoneList().get(i));

                telList.put(tel);
            }
            json.put(KEY_TELS, telList);
        }

        json.put(KEY_BIRTHDAY, t.getBirthday());
        json.put(KEY_IS_HIDE_YEAR, t.isHideYear() ? true : false);
        json.put(KEY_LUNAR_BDAY, t.getLunarBirthDay());
        json.put(KEY_IS_LUNAR, t.isNeedLunarBirthDay() ? true : false);
        json.put(KEY_USER_STATUS, t.getUserStatus());
        json.put(KEY_COMPLETED, t.getCompleted());
        json.put(KEY_SEND_CARD_COUNT, t.getSendCardCount());

        return json;
    }

    public JSONObject toContactJSONObject(Contact t) throws JSONException {
        JSONObject json = new JSONObject();
        if (t.getLastName() != null) {
            json.put(KEY_FAMILY_NAME, t.getLastName());
        } else {
            json.put(KEY_FAMILY_NAME, "");
        }
        if (t.getFirstName() != null) {
            json.put(KEY_GIVEN_NAME, t.getFirstName());
        } else {
            json.put(KEY_GIVEN_NAME, "");
        }
        if (t.getFormatName() != null) {
            json.put(KEY_FORMATTED_NAME, t.getFormatName());
        } else {
            json.put(KEY_FORMATTED_NAME, "");
        }

        json.put(KEY_NICKNAME, t.getNickName());
        json.put(KEY_BIRTHDAY, t.getBirthday());

        Avatar avatar = t.getAvatar();
        if (avatar != null) {
            byte[] image = avatar.getMomoAvatarImage();
            if (image != null && image.length > 0) {
                String avatarB64 = Base64.encodeToString(image, Base64.DEFAULT);
                Log.i("contact", "avatar b64 size:" + avatarB64.length());
                json.put(KEY_AVATAR_B64, avatarB64);
            }
        }

        json.put(KEY_ORGANIZATION, t.getOrganization());
        json.put(KEY_DEPARTMENT, t.getDepartment());
        json.put(KEY_TITLE, t.getJobTitle());
        json.put(KEY_NOTE, t.getNote());
        json.putOpt(KEY_MODIFIED_AT, t.getModifyDate());
        json.put(KEY_EMAILS, t.convertFieldListToJsonArray(Contact.ListField.EMAIL));

        json.put(KEY_TELS, t.convertFieldListToJsonArray(Contact.ListField.PhoneNumber));
        json.put(KEY_ADDRESSES, t.convertFieldListToJsonArray(Contact.ListField.ADDRESS));
        json.put(KEY_IMS, t.convertFieldListToJsonArray(Contact.ListField.IM));
        json.put(KEY_EVENTS, t.convertFieldListToJsonArray(Contact.ListField.EVENTS));
        json.put(KEY_URLS, t.convertFieldListToJsonArray(Contact.ListField.WEBSITE));
        json.put(KEY_RELATIONS, t.convertFieldListToJsonArray(Contact.ListField.RELATIONS));

        return json;
    }

}
