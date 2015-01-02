
package cn.com.nd.momo.api.sync;

public class SQLCreator {
    public final static String CALL_HISTORY = " CREATE TABLE \"call_history\" "
            + "(\"call_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
            + "\"contact_id\" INTEGER, " + "\"number\" VARCHAR(32), "
            + "\"number_type\" INTEGER, " + "\"location\" VARCHAR(32), "
            + "\"operator\" VARCHAR(32), " + "\"call_type\" INTEGER, "
            + "\"date\" INTEGER, " + "\"duration\" INTEGER, "
            + "\"name\" VARCHAR(32), " + "\"create_state\" INTEGER, "
            + "\"update_state\" INTEGER, " + "\"delete_state\" INTEGER); ";

    public final static String SYNC_HISTORY = " CREATE TABLE \"sync_history\" "
            + "(\"sync_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
            + "\"result\" String); ";

    public final static String CONTACT = "CREATE TABLE IF NOT EXISTS \"contact\" "
            + "(\"id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
            + "\"contact_id\" INTEGER UNIQUE NOT NULL , "
            + "\"phone_cid\" INTEGER , " + "\"uid\" INTEGER , "
            + "\"first_name\" VARCHAR(32), " + "\"last_name\" VARCHAR(32), "
            + "\"name\" VARCHAR(32), " + "\"format_name\" VARCHAR(32), "
            + "\"is_friend\" INTEGER  DEFAULT 0, "
            + "\"is_saved_to_local\" INTEGER  DEFAULT 0, "
            + "\"organization\" VARCHAR(255), "
            + "\"department\" VARCHAR(256), " + "\"note\" VARCHAR(256), "
            + "\"birthday\" VARCHAR(10), " + "\"job_title\" VARCHAR(75), "
            + "\"nick_name\" VARCHAR(256), " + "\"modify_date\" INTEGER, "
            + "\"phone_crc\" String, " + "\"category_ids\" String, "
            + "\"custom_ringtone\" String, "
            + "\"user_status\" INTEGER DEFAULT 0, "
            + "\"user_link\" INTEGER DEFAULT 0, "
            + "\"gender\" INTEGER DEFAULT 0, " + "\"residence\" String, "
            + "\"lunar_birthday\" String, " + "\"health_status\" INTEGER DEFAULT 1, "
            + "\"animal_sign\" VARCHAR(10), " + "\"zodiac\" VARCHAR(10), "
            + "\"is_lunar\" INTEGER DEFAULT 0, "
            + "\"starred\" INTEGER DEFAULT 0) ;";

    public final static String CONTACT_PHONE_CID_IDX = "CREATE INDEX [contact_phone_cid_idx] On [contact] ( [phone_cid] );";

    public final static String CONTACT_ID_IDX = "CREATE INDEX [contact_id_idx] On [contact] ( [contact_id] );";

    public final static String CONTACT_UID_IDX = "CREATE INDEX [contact_uid_idx] On [contact] ( [uid] );";

    public final static String DATA = "CREATE TABLE [data] ( "
            + "[row_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + "[contact_row_id] INTEGER NOT NULL,  "
            + "[identifier] INTEGER NOT NULL,  "
            + "[property] INTEGER NOT NULL,  " + "[label] VARCHAR(1024),  "
            + "[value] VARCHAR(1024)); ";

    public final static String DATA_CONTACT_ID_IDX = "CREATE INDEX [data_contact_id_idx] ON [data] ([contact_row_id]);";

    public final static String DATA_CONTACT_ID_PROPERTY_IDX = "CREATE INDEX [data_contact_id_property_idx] ON [data] ([contact_row_id], [property]);";

    public final static String DATA_PROPERTY_IDX = "CREATE INDEX [data_property_idx] ON [data] ([property]);";

    public final static String IMAGE = "CREATE TABLE [image] ( "
            + "[image_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + "[image_contact_id] INTEGER NOT NULL, "
            + "[server_avatar_url] VARCHAR(1024), "
            + "[momo_avatar_image] BLOB);";

    public final static String IMAGE_CONTACT_ID_IDX = "CREATE INDEX [image_contact_id_idx] ON [image] ([image_contact_id]);";

    public final static String PROFILE = "CREATE TABLE \"profile\" (\"key\" VARCHAR NOT NULL, \"value\" VARCHAR);";

    public final static String PROFILE_KEY_IDX = "CREATE UNIQUE INDEX [profile_key_idx] ON [profile] ([key]); ";

    public final static String CARD = "CREATE TABLE [card] ( "
            + "[user_id] INTEGER NOT NULL PRIMARY KEY, "
            + "[content] VARCHAR(1024), "
            + "[validity] INTEGER DEFAULT 0);";

    public final static String CARD_USER = "CREATE TABLE [card_user] ( "
            + "[mobile] VARCHAR(1024), "
            + "[user_id] INTEGER NOT NULL);";

    public final static String CARD_USER_MOBILE_IDX = "CREATE INDEX [card_user_mobile_idx] ON [card_user] ([mobile]);";

    public final static String TABLE_CARD = "card";

    public final static String TABLE_USER_CARD = "card_user";

    public final static String ROBOT = "CREATE TABLE [robot] ( "
            + "[robot_id] INTEGER NOT NULL PRIMARY KEY, "
            + "[app_id] INTEGER NOT NULL, "
            + "[subscribe] INTEGER NOT NULL, "
            + "[robot_info] VARCHAR(1024));";

    public final static String TABLE_ROBOT = "robot";
}
