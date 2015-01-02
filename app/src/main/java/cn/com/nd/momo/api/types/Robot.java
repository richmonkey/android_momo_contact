
package cn.com.nd.momo.api.types;

public class Robot implements Comparable<Robot>, MomoType {
    long id; // 机器人用户id

    String name = ""; // 机器人名

    String avatar = ""; // 机器人头像

    String command = ""; //

    String command_type = ""; //

    String auto_query_command = ""; //

    boolean isSubscribed = false; // 是否订阅了机器人

    public Robot() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommandType() {
        return command_type;
    }

    public void setCommandType(String commandType) {
        command_type = commandType;
    }

    public String getAutoQueryCommand() {
        return auto_query_command;
    }

    public void setAutoQueryCommand(String autoQueryCommand) {
        auto_query_command = autoQueryCommand;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setIsSubscribed(boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
    }

    @Override
    public int compareTo(Robot another) {
        // TODO Auto-generated method stub
        return 0;
    }

}
