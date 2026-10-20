package network;

public class NetworkMessage {
    public enum Type { STATE_UPDATE,
        MOVE_REQUEST,
        ADD_ROBOT_REQUEST,
        REMOVE_ROBOT_REQUEST,
        ROBOT_REMOVED,
        SYNC_INIT }
    public Type type;
    public String payload;

    public NetworkMessage(Type type, String payload) {
        this.type = type;
        this.payload = payload;
    }
}