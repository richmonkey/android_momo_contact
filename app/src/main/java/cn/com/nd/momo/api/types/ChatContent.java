
package cn.com.nd.momo.api.types;

import org.json.JSONObject;

/**
 * 聊天内容类
 * 
 * @date Oct 10, 2011
 * @author Tsung Wu <tsung.bz@gmail.com>
 */
public class ChatContent implements MomoType {
    /**
     * 私聊详情里多条聊天内容才显示一次时间（两条消息时间间隔超过一定程度）
     * 
     * @date Oct 11, 2011
     * @author Tsung Wu <tsung.bz@gmail.com>
     */
    public static class Time extends ChatContent {
    }

    /**
     * 未知聊天类型，保证数据不会丢，新版本就可以展示了
     * 
     * @date Oct 28, 2011
     * @author Tsung Wu <tsung.bz@gmail.com>
     */
    public static class Unknown extends ChatContent {
        private JSONObject content;

        public void setContent(JSONObject content) {
            this.content = content;
        }

        public JSONObject getContent() {
            return content;
        }
    }

    public static class Picture extends ChatContent {
        private String url;

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    public static class Audio extends ChatContent {
        private String url;

        private long duration;

        private boolean isPlayed = false;

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public long getDuration() {
            return duration;
        }

        public boolean isPlayed() {
            return isPlayed;
        }

        public void setPlayed(boolean isPlayed) {
            this.isPlayed = isPlayed;
        }
    }

    public static class File extends ChatContent {
        private String url;

        private String mime;

        private long size;

        private String name;

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setMime(String mime) {
            this.mime = mime;
        }

        public String getMime() {
            return mime;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getSize() {
            return size;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Card extends ChatContent {
        private String uid;

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUid() {
            return uid;
        }
    }

    public static class Location extends ChatContent {
        private double longitude;

        private double latitude;

        private String address = "";

        private boolean isCorrect = false;

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public void setCorrect(boolean isCorrect) {
            this.isCorrect = isCorrect;
        }
    }

    private static class BaseText extends ChatContent {
        private String text;

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public static class Text extends BaseText {
    }

    public static class LongText extends BaseText {
    }

    public static class AudioFrame extends ChatContent {
        private long length = 0;

        private long offset = 0;

        private long duration = 0;

        public long getOffset() {
            return offset;
        }

        public void setOffset(long offset) {
            this.offset = offset;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }

    public static class MobileModify extends ChatContent {
        private String zoneCodeOld;

        private String zoneCode;

        private String mobileOld;

        private String mobile;

        private String text;

        public void setZoneCodeOld(String zoneCodeOld) {
            this.zoneCodeOld = zoneCodeOld;
        }

        public String getZoneCodeOld() {
            return zoneCodeOld;
        }

        public void setZoneCode(String zoneCode) {
            this.zoneCode = zoneCode;
        }

        public String getZoneCode() {
            return zoneCode;
        }

        public void setMobileOld(String mobileOld) {
            this.mobileOld = mobileOld;
        }

        public String getMobileOld() {
            return mobileOld;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getMobile() {
            return mobile;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public static class Contact extends ChatContent {
        private String formattedName;

        private String familyName;

        private String givenName;

        private String middleName;

        private String nickName;

        private String prefix;

        private String suffix;

        private String sort;

        private String phonetic;

        private String birthday;

        private String avatar;

        private String organization;

        private String department;

        private String title;

        private String note;

        private Group<Email> emails;

        private Group<Tel> tels;

        private Group<Address> addresses;

        private Group<Url> urls;

        private Group<Im> ims;

        private Group<Event> events;

        public String getFormattedName() {
            return formattedName;
        }

        public void setFormattedName(String formattedName) {
            this.formattedName = formattedName;
        }

        public String getFamilyName() {
            return familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        public String getGivenName() {
            return givenName;
        }

        public void setGivenName(String givenName) {
            this.givenName = givenName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getPhonetic() {
            return phonetic;
        }

        public void setPhonetic(String phonetic) {
            this.phonetic = phonetic;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getOrganization() {
            return organization;
        }

        public void setOrganization(String organization) {
            this.organization = organization;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public Group<Email> getEmails() {
            return emails;
        }

        public void setEmails(Group<Email> emails) {
            this.emails = emails;
        }

        public Group<Tel> getTels() {
            return tels;
        }

        public void setTels(Group<Tel> tels) {
            this.tels = tels;
        }

        public Group<Address> getAddresses() {
            return addresses;
        }

        public void setAddresses(Group<Address> addresses) {
            this.addresses = addresses;
        }

        public Group<Url> getUrls() {
            return urls;
        }

        public void setUrls(Group<Url> urls) {
            this.urls = urls;
        }

        public Group<Im> getIms() {
            return ims;
        }

        public void setIms(Group<Im> ims) {
            this.ims = ims;
        }

        public Group<Event> getEvents() {
            return events;
        }

        public void setEvents(Group<Event> events) {
            this.events = events;
        }

        public Group<Relation> getRelations() {
            return relations;
        }

        public void setRelations(Group<Relation> relations) {
            this.relations = relations;
        }

        private Group<Relation> relations;

        public static class Base implements MomoType {
            private String type;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            private String value;
        }

        public static class Email extends Base {
        }

        public static class Relation extends Base {
        }

        public static class Event extends Base {
        }

        public static class Url extends Base {
        }

        public static class Im extends Base {
            private String protocol;

            public String getProtocol() {
                return protocol;
            }

            public void setProtocol(String protocol) {
                this.protocol = protocol;
            }
        }

        public static class Tel extends Base {
            // 是否主要电话
            private boolean pref;

            public boolean isPref() {
                return pref;
            }

            public void setPref(boolean pref) {
                this.pref = pref;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getSearch() {
                return search;
            }

            public void setSearch(String search) {
                this.search = search;
            }

            private String city;

            // FIXME 格式化后的电话(字符串，格式为+8613763890000)
            private String search;
        }

        public static class Address implements MomoType {
            private String type;

            private String country;

            private String region;

            private String city;

            private String street;

            private String postal;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getRegion() {
                return region;
            }

            public void setRegion(String region) {
                this.region = region;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getStreet() {
                return street;
            }

            public void setStreet(String street) {
                this.street = street;
            }

            public String getPostal() {
                return postal;
            }

            public void setPostal(String postal) {
                this.postal = postal;
            }
        }
    }
}
