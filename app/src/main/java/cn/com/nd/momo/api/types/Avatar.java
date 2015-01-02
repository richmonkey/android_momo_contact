
package cn.com.nd.momo.api.types;

public class Avatar {
    private int avatarId;

    private long contactId;

    private String serverAvatarURL = "";

    private byte[] momoAvatarImage;

    public Avatar(long contactId, String serverAvatarURL, byte[] momoAvatarImage) {
        super();
        this.contactId = contactId;
        this.serverAvatarURL = serverAvatarURL;
        this.momoAvatarImage = momoAvatarImage;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public byte[] getMomoAvatarImage() {
        return momoAvatarImage;
    }

    public void setMomoAvatarImage(byte[] momoAvatarImage) {
        this.momoAvatarImage = momoAvatarImage;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public String getServerAvatarURL() {
        return serverAvatarURL;
    }

    public void setServerAvatarURL(String serverAvatarURL) {
        this.serverAvatarURL = serverAvatarURL;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + avatarId;
        result = prime * result
                + ((momoAvatarImage == null) ? 0 : momoAvatarImage.hashCode());
        result = prime * result
                + ((serverAvatarURL == null) ? 0 : serverAvatarURL.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Avatar other = (Avatar)obj;
        if (avatarId != other.avatarId)
            return false;

        if (momoAvatarImage == null) {
            if (other.momoAvatarImage != null)
                return false;
        } else if (!momoAvatarImage.equals(other.momoAvatarImage))
            return false;
        if (serverAvatarURL == null) {
            if (other.serverAvatarURL != null)
                return false;
        } else if (!serverAvatarURL.equals(other.serverAvatarURL))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Avatar [avatarId=" + avatarId + ", serverAvatarURL="
                + serverAvatarURL + ", momoAvatarURL=" + momoAvatarImage + "]";
    }

}
