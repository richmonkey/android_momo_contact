
package cn.com.nd.momo.api.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import cn.com.nd.momo.api.types.ChatContent;
import cn.com.nd.momo.api.types.ChatContent.Audio;
import cn.com.nd.momo.api.types.ChatContent.AudioFrame;
import cn.com.nd.momo.api.types.ChatContent.Card;
import cn.com.nd.momo.api.types.ChatContent.File;
import cn.com.nd.momo.api.types.ChatContent.Location;
import cn.com.nd.momo.api.types.ChatContent.LongText;
import cn.com.nd.momo.api.types.ChatContent.Picture;
import cn.com.nd.momo.api.types.ChatContent.Text;
import cn.com.nd.momo.api.types.ChatContent.Unknown;

public class ChatContentParser extends AbstractParser<ChatContent> {

    @Override
    public ChatContent parse(JSONObject json) throws JSONException {
        if (json.has("text")) {
            Text text = new Text();
            text.setText(json.getString("text"));
            return text;
        } else if (json.has("picture")) {
            Picture picture = new Picture();
            picture.setUrl(json.getJSONObject("picture").getString("url"));
            return picture;
        } else if (json.has("audio")) {
            Audio audio = new Audio();
            JSONObject jAudio = json.getJSONObject("audio");
            audio.setDuration(jAudio.getLong("duration"));
            if (jAudio.has("url")) {
                audio.setUrl(jAudio.getString("url"));
            }
            if (jAudio.has("isPlayed")) {
                try {
                    audio.setPlayed(jAudio.getBoolean("isPlayed"));
                } catch (JSONException e) {
                    audio.setPlayed(jAudio.getInt("isPlayed") == 1);
                }
            } else {
                // 旧数据没这个字段，默认认为播放过
                audio.setPlayed(true);
            }
            return audio;
        } else if (json.has("audio_frame")) {
            AudioFrame audioFrame = new AudioFrame();
            JSONObject jsonFrame = json.getJSONObject("audio_frame");
            audioFrame.setLength(jsonFrame.getLong("length"));
            audioFrame.setOffset(jsonFrame.getLong("offset"));
            if (jsonFrame.has("duration"))
                audioFrame.setDuration(jsonFrame.getLong("duration"));
            return audioFrame;
        } else if (json.has("file")) {
            File file = new File();
            file.setUrl(json.getJSONObject("file").getString("url"));
            file.setMime(json.getJSONObject("file").getString("mime"));
            file.setSize(json.getJSONObject("file").getLong("size"));
            file.setName(json.getJSONObject("file").getString("name"));
            return file;
        } else if (json.has("location")) {
            Location location = new Location();
            location.setLatitude(json.getJSONObject("location").getDouble(
                    "latitude"));
            location.setLongitude(json.getJSONObject("location").getDouble(
                    "longitude"));
            location.setAddress(json.getJSONObject("location").optString("address"));
            location.setCorrect(json.getJSONObject("location").optInt("is_correct", 0) == 1);
            return location;
        } else if (json.has("sender_card")) {
            Card card = new Card();
            card.setUid(json.getJSONObject("sender_card").getString("id"));
            return card;
        } else if (json.has("text_long")) {
            LongText longText = new LongText();
            longText.setText(json.getString("text_long"));
            return longText;
        } else if (json.has("mobile_modify")) {
            ChatContent.MobileModify mobileModify = new ChatContent.MobileModify();
            JSONObject mm = json.getJSONObject("mobile_modify");
            mobileModify.setMobile(mm.getString("mobile"));
            mobileModify.setMobileOld(mm.getString("mobile_old"));
            mobileModify.setZoneCode(mm.getString("zone_code"));
            mobileModify.setZoneCodeOld(mm.getString("zone_code_old"));
            mobileModify.setText(mm.getString("text"));
            return mobileModify;
        } else if (json.has("contact")) {
            ChatContent.Contact contact = new ChatContactParser().parse(json
                    .getJSONObject("contact"));
            return contact;
        } else {
            Unknown unknown = new Unknown();
            unknown.setContent(json);
            return unknown;
        }
    }

    @Override
    public JSONObject toJSONObject(ChatContent chatContent) throws JSONException {
        JSONObject content = new JSONObject();
        if (chatContent != null) {
            if (chatContent instanceof Text) {
                content.put("text", ((Text)chatContent).getText());
            } else if (chatContent instanceof Picture) {
                JSONObject picture = new JSONObject();
                picture.put("url", ((Picture)chatContent).getUrl());
                content.put("picture", picture);
            } else if (chatContent instanceof Audio) {
                JSONObject audio = new JSONObject();
                String audioUrl = ((Audio)chatContent).getUrl();
                if (!TextUtils.isEmpty(audioUrl)) {
                    audio.put("url", audioUrl);
                }
                audio.put("duration", ((Audio)chatContent).getDuration());
                audio.put("isPlayed", ((Audio)chatContent).isPlayed());
                content.put("audio", audio);
            } else if (chatContent instanceof AudioFrame) {
                JSONObject audioFrame = new JSONObject();
                audioFrame.put("length", ((AudioFrame)chatContent).getLength());
                audioFrame.put("offset", ((AudioFrame)chatContent).getOffset());
                audioFrame.put("duration", ((AudioFrame)chatContent).getDuration());
                content.put("audio_frame", audioFrame);
            } else if (chatContent instanceof File) {
                JSONObject file = new JSONObject();
                file.put("url", ((File)chatContent).getUrl());
                file.put("mime", ((File)chatContent).getMime());
                file.put("size", ((File)chatContent).getSize());
                file.put("name", ((File)chatContent).getName());
                content.put("file", file);
            } else if (chatContent instanceof Location) {
                JSONObject location = new JSONObject();
                location.put("longitude", ((Location)chatContent).getLongitude());
                location.put("latitude", ((Location)chatContent).getLatitude());
                location.put("address", ((Location)chatContent).getAddress());
                location.put("is_correct", ((Location)chatContent).isCorrect() ? 1 : 0);
                content.put("location", location);
            } else if (chatContent instanceof Card) {
                JSONObject card = new JSONObject();
                card.put("id", ((Card)chatContent).getUid());
                content.put("sender_card", card);
            } else if (chatContent instanceof LongText) {
                content.put("text_long", ((LongText)chatContent).getText());
            } else if (chatContent instanceof ChatContent.MobileModify) {
                ChatContent.MobileModify mModify = (ChatContent.MobileModify)chatContent;
                JSONObject mobileModify = new JSONObject();
                mobileModify.put("mobile", mModify.getMobile());
                mobileModify.put("mobile_old", mModify.getMobileOld());
                mobileModify.put("zone_code", mModify.getZoneCode());
                mobileModify.put("zone_code_old", mModify.getZoneCodeOld());
                mobileModify.put("text", mModify.getText());
                content.put("mobile_modify", mobileModify);
            } else if (chatContent instanceof ChatContent.Contact) {
                ChatContent.Contact contact = (ChatContent.Contact)chatContent;
                content.put("contact", new ChatContactParser().toJSONObject(contact));
            } else if (chatContent instanceof Unknown) {
                JSONObject unknown = ((Unknown)chatContent).getContent();
                return unknown;
            }
        }
        return content;
    }

}
