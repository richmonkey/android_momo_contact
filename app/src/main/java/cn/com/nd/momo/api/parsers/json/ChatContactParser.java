
package cn.com.nd.momo.api.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.types.ChatContent;

/**
 * 联系人数据转换
 * 
 * @date Apr 1, 2012
 * @author Tsung Wu <tsung.bz@gmail.com>
 */
public class ChatContactParser extends AbstractParser<ChatContent.Contact> {

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    @Override
    public ChatContent.Contact parse(JSONObject json) throws JSONException {
        ChatContent.Contact contact = new ChatContent.Contact();
        if (json.has("addresses")) {
            contact.setAddresses(new GroupParser(new AddressParser()).parse(json
                    .getJSONArray("addresses")));
        }
        contact.setAvatar(json.optString("avatar"));
        contact.setBirthday(json.optString("birthday"));
        contact.setDepartment(json.optString("department"));
        if (json.has("emails")) {
            contact.setEmails(new GroupParser(new EmailParser()).parse(json.getJSONArray("emails")));
        }
        if (json.has("events")) {
            contact.setEvents(new GroupParser(new EventParser()).parse(json.getJSONArray("events")));
        }
        contact.setFamilyName(json.optString("family_name"));
        contact.setFormattedName(json.optString("formatted_name"));
        contact.setGivenName(json.optString("given_name"));
        if (json.has("ims")) {
            contact.setIms(new GroupParser(new ImParser()).parse(json.getJSONArray("ims")));
        }
        contact.setMiddleName(json.optString("middle_name"));
        contact.setNickName(json.optString("nickname"));
        contact.setNote(json.optString("note"));
        contact.setOrganization(json.optString("organization"));
        contact.setPhonetic(json.optString("phonetic"));
        contact.setPrefix(json.optString("prefix"));
        if (json.has("relations")) {
            contact.setRelations(new GroupParser(new RelationParser()).parse(json
                    .getJSONArray("relations")));
        }
        contact.setSort(json.optString("sort"));
        contact.setSuffix(json.optString("suffix"));
        if (json.has("tels")) {
            contact.setTels(new GroupParser(new TelParser()).parse(json.getJSONArray("tels")));
        }
        contact.setTitle(json.optString("title"));
        if (json.has("urls")) {
            contact.setUrls(new GroupParser(new UrlParser()).parse(json.getJSONArray("urls")));
        }
        return contact;
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    @Override
    public JSONObject toJSONObject(ChatContent.Contact contact) throws JSONException {
        JSONObject json = new JSONObject();
        if (contact.getAddresses() != null) {
            json.put("addresses",
                    new GroupParser(new AddressParser()).toJSONArray(contact.getAddresses()));
        }
        json.put("avatar", contact.getAvatar());
        json.put("birthday", contact.getBirthday());
        json.put("department", contact.getDepartment());
        if (contact.getEmails() != null) {
            json.put("emails", new GroupParser(new EmailParser()).toJSONArray(contact.getEmails()));
        }
        if (contact.getEvents() != null) {
            json.put("events", new GroupParser(new EventParser()).toJSONArray(contact.getEvents()));
        }
        json.put("family_name", contact.getFamilyName());
        json.put("formatted_name", contact.getFormattedName());
        json.put("given_name", contact.getGivenName());
        if (contact.getIms() != null) {
            json.put("ims", new GroupParser(new ImParser()).toJSONArray(contact.getIms()));
        }
        json.put("middle_name", contact.getMiddleName());
        json.put("nickname", contact.getNickName());
        json.put("note", contact.getNote());
        json.put("organization", contact.getOrganization());
        json.put("phonetic", contact.getPhonetic());
        json.put("prefix", contact.getPrefix());
        if (contact.getRelations() != null) {
            json.put("relations",
                    new GroupParser(new RelationParser()).toJSONArray(contact.getRelations()));
        }
        json.put("sort", contact.getSort());
        json.put("suffix", contact.getSuffix());
        if (contact.getTels() != null) {
            json.put("tels", new GroupParser(new TelParser()).toJSONArray(contact.getTels()));
        }
        json.put("title", contact.getTitle());
        if (contact.getUrls() != null) {
            json.put("urls", new GroupParser(new UrlParser()).toJSONArray(contact.getUrls()));
        }
        return json;
    }

    public static class BaseParser<TBase extends ChatContent.Contact.Base> extends
            AbstractParser<TBase> {
        private Class<TBase> clazz;

        /**
         * 传入类型，不然编译器不知道如何构造
         * 
         * @param clazz
         */
        public BaseParser(Class<TBase> clazz) {
            this.clazz = clazz;
        }

        @Override
        public TBase parse(JSONObject json) throws JSONException {
            TBase base = null;
            try {
                base = clazz.newInstance();
                base.setType(json.optString("type"));
                base.setValue(json.optString("value"));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            return base;
        }

        @Override
        public JSONObject toJSONObject(TBase base) throws JSONException {
            JSONObject json = new JSONObject();
            json.put("type", base.getType());
            json.put("value", base.getValue());
            return json;
        }
    }

    public static class EmailParser extends BaseParser<ChatContent.Contact.Email> {
        public EmailParser() {
            super(ChatContent.Contact.Email.class);
        }
    }

    public static class UrlParser extends BaseParser<ChatContent.Contact.Url> {
        public UrlParser() {
            super(ChatContent.Contact.Url.class);
        }
    }

    public static class RelationParser extends BaseParser<ChatContent.Contact.Relation> {
        public RelationParser() {
            super(ChatContent.Contact.Relation.class);
        }
    }

    public static class EventParser extends BaseParser<ChatContent.Contact.Event> {
        public EventParser() {
            super(ChatContent.Contact.Event.class);
        }
    }

    public static class ImParser extends BaseParser<ChatContent.Contact.Im> {

        public ImParser() {
            super(ChatContent.Contact.Im.class);
        }

        @Override
        public ChatContent.Contact.Im parse(JSONObject json) throws JSONException {
            ChatContent.Contact.Im im = super.parse(json);
            im.setProtocol(json.optString("protocol"));
            return im;
        }

        @Override
        public JSONObject toJSONObject(ChatContent.Contact.Im im) throws JSONException {
            JSONObject json = super.toJSONObject(im);
            json.put("protocol", im.getProtocol());
            return json;
        }

    }

    public static class TelParser extends BaseParser<ChatContent.Contact.Tel> {

        public TelParser() {
            super(ChatContent.Contact.Tel.class);
        }

        @Override
        public ChatContent.Contact.Tel parse(JSONObject json) throws JSONException {
            ChatContent.Contact.Tel tel = super.parse(json);
            tel.setCity(json.optString("city"));
            tel.setPref(json.optBoolean("pref"));
            tel.setSearch(json.optString("search"));
            return tel;
        }

        @Override
        public JSONObject toJSONObject(ChatContent.Contact.Tel tel) throws JSONException {
            JSONObject json = super.toJSONObject(tel);
            json.put("city", tel.getCity());
            json.put("pref", tel.isPref());
            json.put("search", tel.getSearch());
            return json;
        }

    }

    public static class AddressParser extends AbstractParser<ChatContent.Contact.Address> {

        @Override
        public ChatContent.Contact.Address parse(JSONObject json) throws JSONException {
            ChatContent.Contact.Address address = new ChatContent.Contact.Address();
            address.setCity(json.optString("city"));
            address.setCountry(json.optString("country"));
            address.setPostal(json.optString("postal"));
            address.setRegion(json.optString("region"));
            address.setStreet(json.optString("street"));
            address.setType(json.optString("type"));
            return address;
        }

        @Override
        public JSONObject toJSONObject(ChatContent.Contact.Address address) throws JSONException {
            JSONObject json = new JSONObject();
            json.put("city", address.getCity());
            json.put("country", address.getCountry());
            json.put("postal", address.getPostal());
            json.put("region", address.getRegion());
            json.put("street", address.getStreet());
            json.put("type", address.getType());
            return json;
        }
    }
}
